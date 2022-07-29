package dev.atedeg.mdm.milkplanning.types

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
 * @note it must be a [[PositiveDecimal positive decimal number]].
 * @example `QuintalsOfMilk(1.1)` is a valid weight of 110 kg.
 * @example `QuintalsOfMilk(-20.5)` is not a valid weight.
 */
final case class QuintalsOfMilk(quintals: NonNegativeNumber) derives Plus, Times, Minus

/**
 * Represent how many [[QuintalsOfMilk quintals of milk]] are needed to produce a given quantity of [[Product product]].
 * @example In order to produce 180kg of a product are necessary 10 quintals of milk, in this case the yield is `5.55`.
 * @example `Yield(0)` is not a valid yield.
 * @example `Yield(5.55)` is a valid yield.
 */
final case class Yield(n: PositiveDecimal)

/**
 * It defines, for each [[Product product]], the [[Yield yield]] of the milk.
 */
type RecipeBook = CheeseType => Yield

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
