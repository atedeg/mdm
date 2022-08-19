package dev.atedeg.mdm.production

import java.util.UUID

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * A production plan that specifies how many [[NumberOfUnits units]] of
 * [[Product products]] need to be produced.
 */
final case class ProductionPlan(plan: NonEmptyList[ProductionPlanItem])

/**
 * A single line of a [[ProductionPlan production plan]] that specifies a
 * [[Product product]] and how many [[NumberOfUnits units]] of it to produce.
 */
final case class ProductionPlanItem(productToProduce: Product, units: NumberOfUnits)

/**
 * Counts the number of units of something.
 */
final case class NumberOfUnits(n: PositiveNumber)

enum Production:
  /**
   * A [[Production production]] that needs to be started, it specifies the [[Product product]] to produce
   * and the [[NumberOfUnits units]] in which it needs to be produced.
   */
  case ToStart(ID: ProductionID, productToProduce: Product, unitsToProduce: NumberOfUnits)

  /**
   * A [[Production production]] that has already started, it specifies the [[Product product]] that is being produced
   * and the [[NumberOfUnits units]] in which it is being produced.
   */
  case InProgress(ID: ProductionID, productInProduction: Product, unitsInProduction: NumberOfUnits)

  /**
   * A [[Production production]] that ended, it has a [[BatchID batch ID]] and specifies the [[Product product]]
   * that was produced and in the [[NumberOfUnits units]] produced.
   */
  case Ended(ID: ProductionID, batchID: BatchID, producedProduct: Product, producedUnits: NumberOfUnits)

/**
 * An ID used to uniquely identify a [[Production production]].
 */
final case class ProductionID(ID: UUID)

/**
 * An ID used to uniquely identify a batch of cheese.
 */
final case class BatchID(ID: UUID)

/**
 * Associates to each [[CheeseType cheese type]] the [[Recipe recipe]] to produce a quintal of it.
 */
final case class RecipeBook(recipeBook: Map[CheeseType, Recipe])

/**
 * A list of [[QuintalsOfIngredient ingredients and the respective quintals]] needed to produce a quintal of a product.
 */
final case class Recipe(lines: NonEmptyList[QuintalsOfIngredient])

/**
 * An [[Ingredient ingredient]] and a [[WeightInQuintals weight in quintals]].
 */
final case class QuintalsOfIngredient(quintals: WeightInQuintals, ingredient: Ingredient)

/**
 * A weight expressed in quintals.
 */
final case class WeightInQuintals(n: PositiveDecimal) derives Times

/**
 * For each [[CheeseType cheese type]] associates it its [[RipeningDays ripening days]].
 */
final case class CheeseTypeRipeningDays(value: Map[CheeseType, RipeningDays])

/**
 * The number of days a [[CheeseType cheese type]] has to ripen before being ready.
 */
final case class RipeningDays(days: NonNegativeNumber)
