package dev.atedeg.mdm.restocking

import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * A quantity of milk expressed in quintals.
 */
final case class QuintalsOfMilk(quintals: PositiveNumber)

/**
 * An [[Ingredient ingredient]] and a [[WeightInQuintals weight in quintals]].
 */
final case class QuintalsOfIngredient(quintals: WeightInQuintals, ingredient: Ingredient)

/**
 * A weight expressed in quintals.
 */
final case class WeightInQuintals(n: PositiveDecimal)

/**
 * Quintals of stocked milk.
 */
final case class StockedMilk(quintals: NonNegativeDecimal)

/**
 * The quantity of ingredients in stock.
 */
type Stock = Map[Ingredient, StockedQuantity]

/**
 * A stocked quantity.
 */
final case class StockedQuantity(quintals: NonNegativeDecimal) derives Minus
