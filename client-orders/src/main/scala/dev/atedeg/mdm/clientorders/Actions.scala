package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import scala.annotation.tailrec

import cats.Monad
import cats.data.NonEmptyList
import cats.kernel.Comparison.*
import cats.syntax.all.*

import dev.atedeg.mdm.clientorders.InProgressOrderLine.*
import dev.atedeg.mdm.clientorders.OrderCompletionError.*
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
def priceOrder(priceList: PriceList)(incomingOrder: IncomingOrder): PricedOrder =
  val pricedOrderLines = incomingOrder.orderLines.map { case iol @ IncomingOrderLine(quantity, product) =>
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
 * Turns a [[order.PricedOrder priced order]] into an [[order.InProgressOrder in-progress order]] that can then be
 * fulfilled by operators.
 *
 * @param pricedOrder
 *   the priced order to be marked as in progress.
 */
def startPreparingOrder(pricedOrder: PricedOrder): InProgressOrder =
  val PricedOrder(id, ol, customer, deliveryDate, deliveryLocation, totalPrice) = pricedOrder
  val newOrderLine = ol.map { case PricedOrderLine(quantity, product, price) =>
    Incomplete(0.palletizedQuantity, quantity, product, price)
  }
  InProgressOrder(id, newOrderLine, customer, deliveryDate, deliveryLocation, totalPrice)

/**
 * Palletizes a [[Product product]] in the specified [[order.Quantity quantity]].
 *
 * @param inProgressOrder
 *   the order for which the product needs to be palletized.
 * @param quantity
 *   the quantity of product to be palletized.
 * @param product
 *   the product to be palletized.
 * @return
 *   an [[order.InProgressOrder in-progress order]] where the corresponding [[order.InProgressOrderLine line]] has been
 *   updated with the [[order.Quantity specified quantity]].
 */
def palletizeProductForOrder[M[_]: CanRaise[PalletizationError]: Monad](
    inProgressOrder: InProgressOrder,
)(quantity: Quantity, product: Product): M[InProgressOrder] =
  val InProgressOrder(id, ol, customer, dd, dl, totalPrice) = inProgressOrder
  for {
    orderLine <- ol.find(_ == product).ifMissingRaise(ProductNotInOrder())
    updatedLine <- addToLine(orderLine)(quantity)
    newOrderLines = ol.map {
      case i @ Incomplete(_, _, `product`, _) => updatedLine
      case l @ _ => l
    }
  } yield InProgressOrder(id, newOrderLines, customer, dd, dl, totalPrice)

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
 * Completes an [[order.InProgressOrder in-progress order]].
 *
 * @param inProgressOrder
 *   the in-progress order to be marked as complete.
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
 * Computes the total [[order.WeightInKilograms weight]] of a [[order.CompletedOrder complete order]].
 *
 * @param completeOrder
 *   the order whose weight has to be computed.
 */
def weightOrder(completeOrder: CompletedOrder): WeightInKilograms =
  completeOrder.orderLines
    .map(r => (r.product, r.quantity))
    .map { case (product, quantity) => (product.weight.n * quantity.n).toDecimal / 1_000 }
    .map(WeightInKilograms(_))
    .reduce(_ + _)

/**
 * Creates a [[order.TransportDocument transport document]] from a [[order.CompletedOrder complete order]].
 *
 * @param completeOrder
 *   the order for which the transport document has to be created.
 * @param weight
 *   the weight of the order.
 */
def createTransportDocument(completeOrder: CompletedOrder, weight: WeightInKilograms): TransportDocument =
  val CompletedOrder(_, orderLines, customer, _, deliveryLocation, _) = completeOrder
  val transportDocumentLines = orderLines.map(l => TransportDocumentLine(l.quantity, l.product))
  val date = LocalDateTime.now
  TransportDocument(deliveryLocation, mambelliDeliveryLocation, customer, date, transportDocumentLines, weight)

private val mambelliDeliveryLocation = Location(Latitude(12), Longitude(44))
