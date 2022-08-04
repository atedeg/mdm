package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import scala.annotation.tailrec

import cats.Monad
import cats.data.NonEmptyList
import cats.kernel.Comparison.*
import cats.syntax.all.*

import dev.atedeg.mdm.clientorders.InProgressOrderLine.*
import dev.atedeg.mdm.clientorders.OrderCompletionError.*
import dev.atedeg.mdm.clientorders.OutgoingEvent.OrderProcessed
import dev.atedeg.mdm.clientorders.PalletizationError.*
import dev.atedeg.mdm.clientorders.utils.*
import dev.atedeg.mdm.clientorders.utils.QuantityOps.*
import dev.atedeg.mdm.clientorders.utils.QuantityOps.given
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Turns an [[IncomingOrder incoming order]] into a [[PricedOrder priced order]] by computing the price
 * of each [[IncomingOrderLine line]] using a [[PriceList price list]].
 */
def processIncomingOrder[M[_]: Monad: Emits[OrderProcessed]](priceList: PriceList)(
    incomingOrder: IncomingOrder,
): M[PricedOrder] =
  val pricedOrder = priceOrder(priceList)(incomingOrder)
  emit(OrderProcessed(incomingOrder): OrderProcessed).thenReturn(pricedOrder)

private def priceOrder(priceList: PriceList)(incomingOrder: IncomingOrder): PricedOrder =
  val pricedOrderLines = incomingOrder.orderLines.map { case IncomingOrderLine(quantity, product) =>
    val price = priceList(product).n * quantity.n
    PricedOrderLine(quantity, product, price.euroCents)
  }
  val totalPrice = pricedOrderLines.map(_.totalPrice).reduce(_ + _)
  PricedOrder(
    incomingOrder.id,
    pricedOrderLines,
    incomingOrder.customer,
    incomingOrder.deliveryDate,
    incomingOrder.deliveryLocation,
    totalPrice,
  )

/**
 * Turns a [[Order.PricedOrder priced order]] into an [[Order.InProgressOrder in-progress order]] that can then be
 * fulfilled by operators.
 */
def startPreparingOrder(pricedOrder: PricedOrder): InProgressOrder =
  val PricedOrder(id, ol, customer, deliveryDate, deliveryLocation, totalPrice) = pricedOrder
  val newOrderLine = ol.map { case PricedOrderLine(quantity, product, price) =>
    Incomplete(0.palletizedQuantity, quantity, product, price)
  }
  InProgressOrder(id, newOrderLine, customer, deliveryDate, deliveryLocation, totalPrice)

/**
 * Palletizes a [[Product product]] in the specified [[Order.Quantity quantity]] for a given
 * [[InProgressOrder order in progress]]. The result is an [[Order.InProgressOrder in-progress order]]
 * where the corresponding [[Order.InProgressOrderLine line]] has been updated with the
 * [[Order.Quantity specified quantity]].
 */
def palletizeProductForOrder[M[_]: CanRaise[PalletizationError]: Monad](quantity: Quantity, product: Product)(
    inProgressOrder: InProgressOrder,
): M[InProgressOrder] =
  val InProgressOrder(id, ol, customer, dd, dl, totalPrice) = inProgressOrder
  for {
    orderLine <- ol.find(hasProduct(product)).ifMissingRaise(ProductNotInOrder(product))
    updatedLine <- addToLine(orderLine)(quantity)
    newOrderLines = ol.map {
      case Incomplete(_, _, `product`, _) => updatedLine
      case l @ _ => l
    }
  } yield InProgressOrder(id, newOrderLines, customer, dd, dl, totalPrice)

private def hasProduct(product: Product)(ol: InProgressOrderLine): Boolean = ol match
  case Incomplete(_, _, p, _) => p === product
  case Complete(_, p, _) => p === product

private def addToLine[M[_]: Monad: CanRaise[PalletizedMoreThanRequired]](ol: InProgressOrderLine)(
    quantityToAdd: Quantity,
): M[InProgressOrderLine] = ol match
  case _: Complete => raise(PalletizedMoreThanRequired(0.missingQuantity): PalletizedMoreThanRequired)
  case Incomplete(palletized, required, product, price) =>
    val missingQuantity = (required.n.toNonNegative - palletized.n).missingQuantity
    missingQuantity.n.comparison(quantityToAdd.n.toNonNegative) match
      case GreaterThan => Incomplete(palletized + quantityToAdd.toPalletizedQuantity, required, product, price).pure
      case EqualTo => Complete(required, product, price).pure
      case LessThan => raise(PalletizedMoreThanRequired(missingQuantity): PalletizedMoreThanRequired)

/**
 * Completes an [[Order.InProgressOrder in-progress order]].
 */
def completeOrder[Result[_]: CanRaise[OrderCompletionError]: Monad](
    inProgressOrder: InProgressOrder,
): Result[CompletedOrder] =
  val InProgressOrder(id, ol, customer, dd, dl, totalPrice) = inProgressOrder
  for {
    completedOrderLines <- getCompletedOrderLines(ol).ifMissingRaise(OrderNotComplete())
    completeOrderLines = completedOrderLines.map(o => CompleteOrderLine(o.quantity, o.product, o.price))
  } yield CompletedOrder(id, completeOrderLines, customer, dd, dl, totalPrice)

private def getCompletedOrderLines(orderLines: NonEmptyList[InProgressOrderLine]): Option[NonEmptyList[Complete]] =
  @tailrec
  def _getCompletedOrderLines(acc: Option[List[Complete]])(l: List[InProgressOrderLine]): Option[List[Complete]] =
    l match
      case (c @ _: Complete) :: tail => _getCompletedOrderLines(acc.map(c :: _))(tail)
      case (_: Incomplete) :: _ => None
      case Nil => acc

  _getCompletedOrderLines(Some(Nil))(orderLines.toList).flatMap(_.toNel).map(_.reverse)

/**
 * Computes the total [[Order.WeightInKilograms weight]] of a [[Order.CompletedOrder complete order]].
 */
def weightOrder(completeOrder: CompletedOrder): WeightInKilograms =
  completeOrder.orderLines
    .map(r => (r.product, r.quantity))
    .map { case (product, quantity) => (product.weight.n * quantity.n).toDecimal / 1_000 }
    .map(WeightInKilograms(_))
    .reduce(_ + _)

/**
 * Creates a [[Order.TransportDocument transport document]] from a [[Order.CompletedOrder complete order]].
 */
def createTransportDocument(completeOrder: CompletedOrder, weight: WeightInKilograms): TransportDocument =
  val CompletedOrder(_, orderLines, customer, _, deliveryLocation, _) = completeOrder
  val transportDocumentLines = orderLines.map(l => TransportDocumentLine(l.quantity, l.product))
  val date = LocalDateTime.now
  TransportDocument(deliveryLocation, mambelliDeliveryLocation, customer, date, transportDocumentLines, weight)

private val mambelliDeliveryLocation = Location(Latitude(12), Longitude(44))
