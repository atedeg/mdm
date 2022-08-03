package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime

import cats.Monad
import cats.data.NonEmptyList
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
 * @note
 *   It can raise a [[PalletizationError palletization error]].
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
    orderLine <- findOrderLine(product, ol).ifMissingRaise(ProductNotInOrder())
    requiredQuantity = getRequiredQuantity(orderLine)
    totalPriceForProduct = getTotalPriceForProduct(orderLine)
    _ <- (quantity <= requiredQuantity).otherwiseRaise(PalletizedMoreThanRequired(requiredQuantity))
    updatedLine = updateLine(product, quantity, requiredQuantity, totalPriceForProduct)
    newOrderLine = ol.map {
      case i @ Incomplete(_, _, `product`, _) => updatedLine
      case l @ _ => l
    }
  } yield InProgressOrder(id, newOrderLine, customer, dd, dl, totalPrice)

private def isProductInOrder(orderLines: NonEmptyList[InProgressOrderLine], product: Product): Boolean =
  orderLines.map {
    case Complete(_, prod, _) => prod
    case Incomplete(_, _, prod, _) => prod
  }.exists(_ == product)

private def findOrderLine(product: Product, ol: NonEmptyList[InProgressOrderLine]): Option[InProgressOrderLine] =
  ol.find(_ == product)

private def getRequiredQuantity(orderLine: InProgressOrderLine): Quantity = orderLine match {
  case Complete(quantity, _, _) => quantity
  case Incomplete(_, requiredQuantity, _, _) => requiredQuantity
}

private def getTotalPriceForProduct(orderLine: InProgressOrderLine): PriceInEuroCents = orderLine match {
  case Complete(_, _, price) => price
  case Incomplete(_, _, _, price) => price
}

private def updateLine(
    product: Product,
    quantity: Quantity,
    requiredQuantity: Quantity,
    totalPrice: PriceInEuroCents,
): InProgressOrderLine =
  if quantity == requiredQuantity then Complete(quantity, product, totalPrice)
  else Incomplete(quantity.toPalletizedQuantity, requiredQuantity, product, totalPrice)

/**
 * Completes an [[order.InProgressOrder in-progress order]].
 *
 * @note
 *   It can raise an [[OrderCompletionError order completion error]].
 * @param inProgressOrder
 *   the in-progress order to be marked as complete.
 */
def completeOrder[Result[_]: CanRaise[OrderCompletionError]: Monad](
    inProgressOrder: InProgressOrder,
): Result[CompletedOrder] =
  val InProgressOrder(id, ol, customer, dd, dl, totalPrice) = inProgressOrder
  for {
    completedOrderLines <- getCompletedOrderLines(ol).ifMissingRaise(OrderNotComplete())
    completeOrderLines   = ol.map(o => CompleteOrderLine(o.quantity, o.product, o.price))
  } yield CompletedOrder(id, completeOrderLines, customer, dd, dl, totalPrice)

private def getCompletedOrderLines(orderLines: NonEmptyList[InProgressOrderLine]): Option[NonEmptyList[Complete]] =
  def prova(acc: Option[List[Complete]])(l: List[InProgressOrderLine]): Option[List[Complete]] = l match
    case (c @ _: Complete) :: tail => prova(acc.map(c :: _))(tail)
    case (_: Incomplete) :: _ => None
    case Nil => acc

  prova(Some(Nil))(orderLines.toList).flatMap(_.toNel).map(_.reverse)

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
  TransportDocument(
    deliveryLocation,
    mambelliDeliveryLocation,
    customer,
    LocalDateTime.now(),
    transportDocumentLines,
    weight,
  )

private val mambelliDeliveryLocation = Location(Latitude(12), Longitude(44))
