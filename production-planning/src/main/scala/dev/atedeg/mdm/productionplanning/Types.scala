package dev.atedeg.mdm.productionplanning

import dev.atedeg.mdm.utils.{NonNegativeNumber, NumberInClosedRange, PositiveNumber}
import dev.atedeg.mdm.products.{CheeseType, Product}
import cats.data.NonEmptyList

import java.time.LocalDate

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
final case class  Quantity(n: PositiveNumber)

final case class Order(deadline: LocalDate, orderedProducts: NonEmptyList[OrderedProduct])

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

