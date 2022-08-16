package dev.atedeg.mdm.productionplanning

import java.time.LocalDate
import java.util.UUID

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.{ DecimalInClosedRange, NonNegativeNumber, Plus, PositiveNumber, Times }
import dev.atedeg.mdm.utils.given

/**
 * All the [[ProductToProduce products to be produced]] in a day.
 */
final case class ProductionPlan(productsToProduce: NonEmptyList[ProductToProduce])

/**
 * The [[Quantity quantity]] of each [[Product product]] to be produced.
 */
final case class ProductToProduce(product: Product, quantity: Quantity)

/**
 * A quantity of something.
 */
final case class Quantity(n: PositiveNumber) derives Plus, Times

/**
 * The [[Product products]] missing from the stock in a given [[MissingQuantity quantity]].
 */
final case class MissingProducts(missingProducts: Map[Product, MissingQuantity])

/**
 * A quantity of a missing [[Product product]].
 */
final case class MissingQuantity(n: NonNegativeNumber)

/**
 * A set of requested [[Product products]] with the [[Quantity quantities]] that have to be produced by the given
 * [[LocalDate date]].
 */
final case class Order(orderdID: OrderID, requiredBy: LocalDate, orderedProducts: NonEmptyList[OrderedProduct])

/**
 * An ID used to uniquely identify an [[Order order]].
 */
final case class OrderID(id: UUID)

/**
 * A [[Product product]] requested in a given [[Quantity quantity]].
 */
final case class OrderedProduct(product: Product, quantity: Quantity)

/**
 * Defines how many [[RipeningDays days of ripening]] are needed for a given [[CheeseType type of cheese]].
 */
final case class CheeseTypeRipeningDays(value: Map[CheeseType, RipeningDays])

/**
 * The number of days needed for the ripening process to be done.
 */
final case class RipeningDays(days: NonNegativeNumber) derives Plus
