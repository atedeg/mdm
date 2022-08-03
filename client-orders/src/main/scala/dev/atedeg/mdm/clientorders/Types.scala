package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.string.MatchesRegex

import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * A set of [[IncomingOrderLine order lines]] with their respective [[Quantity quantity]] (e.g. 1000 ricotte of 0.5kg,
 * 50 squacqueroni of 1 kg), it also contains data about the [[Customer customer]], an expected
 * [[DateTime delivery date]] and the [[Location delivery location]].
 */
final case class IncomingOrder(
    id: OrderID,
    orderLines: NonEmptyList[IncomingOrderLine],
    customer: Customer,
    deliveryDate: LocalDateTime,
    deliveryLocation: Location,
)

/**
 * The unique identifier of an order which allows one to track the order during its lifecycle.
 */
final case class OrderID(id: UUID)

/**
 * A [[Product product]] with its ordered [[Quantity quantity]].
 */
final case class IncomingOrderLine(quantity: Quantity, product: Product)

/**
 * A quantity of something.
 */
final case class Quantity(n: PositiveNumber)

/**
 * The missing quantity of a [[Product product]] necessary to fullfil an [[InProgressOrderLine in-progress order line]].
 */
final case class MissingQuantity(n: NonNegativeNumber)

/**
 * A physical or legal entity that places [[IncomingOrder orders]].
 */
final case class Customer(code: CustomerID, name: CustomerName, vatNumber: VATNumber)

/**
 * An id which uniquely identifies a [[Customer customer]].
 */
final case class CustomerID(id: UUID)

/**
 * A human readable name used to refer to a [[Customer customer]].
 */
final case class CustomerName(name: String)

/**
 * An alphanumeric code for value-added tax purposes.
 *
 * @see
 *   [[https://en.wikipedia.org/wiki/VAT_identification_number VAT identification number on Wikipedia]] for further
 *   details.
 */
final case class VATNumber(number: String Refined MatchesRegex[VATRegex])
private[clientorders] type VATRegex = "[A-Z]{2}[0-9A-Z]{2,13}"

/**
 * The location where an order has to be shipped to.
 */
final case class Location(latitude: Latitude, longitude: Longitude)

/**
 * A latitude specified in degrees.
 */
final case class Latitude(value: NumberInClosedRange[-90, 90])

/**
 * A longitude specified in degrees.
 */
final case class Longitude(value: NumberInClosedRange[-180, 180])

/**
 * Associates to each [[Product product]] its [[PriceInEuroCents unitary price]].
 */
type PriceList = Product => PriceInEuroCents

/**
 * A price expressed in cents, the smallest currency unit for euros.
 */
final case class PriceInEuroCents(n: PositiveNumber) derives Plus

/**
 * An order where each [[PricedOrderLine line]] has an associated [[PriceInEuroCents price]] and, optionally, an applied
 * [[Discount discount]]. It also has the total [[PriceInEuroCents price]]. Its structure resembles the
 * [[IncomingOrder incoming order]]'s with the difference that each [[PricedOrderLine line]] has been priced.
 */
final case class PricedOrder(
    id: OrderID,
    orderLines: NonEmptyList[PricedOrderLine],
    customer: Customer,
    deliveryDate: LocalDateTime,
    deliveryLocation: Location,
    totalPrice: PriceInEuroCents,
)

/**
 * A [[Product product]] with its [[Quantity quantity]] and a [[PriceInEuroCents price]].
 */
final case class PricedOrderLine(quantity: Quantity, product: Product, totalPrice: PriceInEuroCents)

/**
 * An order that is being fulfilled by an operator. Its structure resembles the [[PricedOrder priced order]]'s with the
 * difference that each [[PricedOrderLine line]] can specify whether it is fulfilled or not.
 */
final case class InProgressOrder(
    id: OrderID,
    orderLines: NonEmptyList[InProgressOrderLine],
    customer: Customer,
    deliveryDate: LocalDateTime,
    deliveryLocation: Location,
    totalPrice: PriceInEuroCents,
)

/**
 * A [[Product product]] with its [[Quantity quantity]] and a [[PriceInEuroCents price]].
 * It may be in two different states: [[InProgressOrderLine.Complete complete]] if the
 * [[Product product]] has already been palletized and is ready in the required [[Quantity quantity]];
 * [[InProgressOrderLine.Incomplete incomplete]] if the [[Product product]] is not present in the required
 * [[Quantity quantity]].
 */
enum InProgressOrderLine:

  /**
   * A [[InProgressOrderLine line]] of an [[InProgressOrder in-progress order]] where the [[Product product]] is ready
   * in the required [[Quantity quantity]] and has been palletized.
   */
  case Complete(quantity: Quantity, product: Product, price: PriceInEuroCents)

  /**
   * A [[InProgressOrderLine line]] of an [[InProgressOrder in-progress order]] where the [[Product product]] is still
   * not available in the required [[Quantity quantity]] but [[PalletizedQuantity a part or none of it]] may have
   * already been palletized.
   */
  case Incomplete(actual: PalletizedQuantity, required: Quantity, product: Product, price: PriceInEuroCents)

/**
 * A quantity (possibly 0) of a palletized [[Product product]].
 */
final case class PalletizedQuantity(n: NonNegativeNumber) derives Plus

/**
 * An order that has been fulfilled by the operator and is ready to be shipped.
 */
final case class CompletedOrder(
    id: OrderID,
    orderLines: NonEmptyList[CompleteOrderLine],
    customer: Customer,
    deliveryDate: LocalDateTime,
    deliveryLocation: Location,
    totalPrice: PriceInEuroCents,
)

/**
 * A [[Product product]] with its [[Quantity quantity]], a [[PriceInEuroCents price]].
 */
final case class CompleteOrderLine(quantity: Quantity, product: Product, price: PriceInEuroCents)

/**
 * A document that has to specify: a [[Location delivery location]], a [[Location shipping location]], the
 * [[Customer customer]]'s info, the [[DateTime shipping date]], the total [[WeightInKilograms weight]] of the pallet,
 * and a list of [[TransportDocumentLine transport document lines]].
 */
final case class TransportDocument(
    deliveryLocation: Location,
    shippingLocation: Location,
    customer: Customer,
    shippingDate: LocalDateTime,
    transportDocumentLines: NonEmptyList[TransportDocumentLine],
    totalWeight: WeightInKilograms,
)

/**
 * A [[Product product]] with its respective shipped [[Quantity quantity]].
 */
final case class TransportDocumentLine(quantity: Quantity, product: Product)

/**
 * A weight expressed in kilograms.
 */
final case class WeightInKilograms(n: PositiveDecimal) derives Plus
