package dev.atedeg.mdm.milkplanning

import java.time.LocalDateTime

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval

import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * The [[QuintalsOfMilk quintals of milk]] processed in order to produce cheese.
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
 */
final case class Yield(n: PositiveDecimal)

/**
 * It defines, for each [[CheeseType cheese type]], the [[Yield yield]] of milk when producing it.
 */
type RecipeBook = Map[CheeseType, Yield]

/**
 * It defines, for each [[Product product]], the [[StockedQuantity quantity available in stock]].
 */
type Stock = Map[Product, StockedQuantity]

/**
 * A quantity of a stocked [[Product product]], it may also be zero.
 */
final case class StockedQuantity(quantity: NonNegativeNumber)

/**
 * A quantity of something.
 */
final case class Quantity(n: PositiveNumber) derives Plus, Times

/**
 * A [[Product product]] requested in a given [[Quantity quantity]] that has to be produced by the given
 * [[LocalDateTime date]].
 */
final case class RequestedProduct(product: Product, quantity: Quantity, requiredBy: LocalDateTime)
