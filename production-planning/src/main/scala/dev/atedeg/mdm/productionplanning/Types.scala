package dev.atedeg.mdm.productionplanning

import dev.atedeg.mdm.utils.{NonNegativeNumber, NumberInClosedRange, Plus, PositiveNumber, Times}
import dev.atedeg.mdm.products.{CheeseType, Product}
import cats.data.NonEmptyList
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

import java.time.LocalDate
import java.util.UUID

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
 * @example `Quantity(-2)` is not a valid quantity.
 * @example `Quantity(5)` is a valida quantity.
 */
final case class  Quantity(n: PositiveNumber) derives Plus, Times

/**
 * It defines, for each [[Product product]], the [[StockedQuantity quantity in stock]].
 */
type Stock = Product => StockedQuantity

/**
 * A quantity of a stocked [[Product product]], it may also be zero.
 * @note it must be a [[NonNegativeNumber non-negative number]].
 * @example `StockedQuantity(0)` is valid.
 * @example `StockedQuantity(-1)` is invalid.
 */
final case class StockedQuantity(n: NonNegativeNumber)

/**
 * A set of requested [[Product product]] with the [[Quantity quantities]] that have to be produced by the given
 * [[LocalDate date]].
 */
final case class Order(orderdID: OrderID, requiredBy: LocalDate, orderedProducts: NonEmptyList[OrderedProduct])

/**
 * Uniquely identifies an [[Order order]].
 */
final case class OrderID(id: UUID)

/**
 * A [[Product product]] requested in a given [[Quantity quantity]].
 */
final case class OrderedProduct(product: Product, quantity: Quantity)

/**
 * Defines how many [[RipeningDays days of ripening]] are needed for a given [[CheeseType type of cheese]].
 */
type CheeseTypeRipeningDays = CheeseType => RipeningDays

/**
 * The number of days needed for the ripening process to be done.
 * @example `RipeningDays(7)` is a valid number of days.
 * @example `RipeningDays(-2)` is not a valid number of days.
 */
final case class RipeningDays(days: NonNegativeNumber)
