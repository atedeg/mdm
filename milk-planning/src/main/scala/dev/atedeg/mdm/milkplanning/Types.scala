package dev.atedeg.mdm.milkplanning

import java.time.LocalDateTime

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval

import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * Milk processed in order to produce cheese.
 */
final case class ProcessedMilk(quantity: QuintalsOfMilk)

/**
 * A quantity of milk expressed in quintals.
 */
final case class QuintalsOfMilk(quintals: NonNegativeNumber) derives Plus, Times, Minus

/**
 * A decimal that represents the yield of milk when producing a given [[CheeseType cheese type]]:
 * i.e. to produce `n` quintals of a given [[CheeseType cheese type]], `yield of cheese type * n`
 * [[QuintalsOfMilk quintals of milk]] must be used.
 * @example `Yield(0)` is not a valid yield.
 * @example `Yield(5.55)` is a valid yield.
 */
final case class Yield(n: PositiveDecimal)

/**
 * It defines, for each [[Product product]], the [[Yield yield]] of the milk.
 */
type RecipeBook = Map[CheeseType, Yield]

/**
 * It defines, for each [[Product product]], the [[StockedQuantity quantity in stock]].
 */
type Stock = Map[Product, StockedQuantity]

/**
 * A quantity of a stocked [[Product product]], it may also be zero.
 * @note it must be a [[NonNegativeNumber non-negative number]].
 * @example `StockedQuantity(0)` is valid.
 * @example `StockedQuantity(-1)` is invalid.
 */
final case class StockedQuantity(quantity: NonNegativeNumber)

/**
 * A quantity of something.
 * @example `Quantity(-2)` is not a valid quantity.
 * @example `Quantity(20)` is a valida quantity.
 */
final case class Quantity(n: PositiveNumber) derives Plus, Times

/**
 * A [[Product product]] requested in a given [[Quantity quantity]] that has to be produced by the given
 * [[LocalDateTime date]].
 */
final case class RequestedProduct(product: Product, quantity: Quantity, requiredBy: LocalDateTime)
