package dev.atedeg.mdm.production

import java.util.UUID

type Product = Int // TODO: shared kernel
type CheeseType = Int // TODO: shared kernel
type Quantity = Int // TODO: maybe is in utils
type WeightInQuintals = Int // TODO: maybe is in utils
type ProductionID = UUID // TODO: make me a case class

enum Production:
  /**
   * A [[Production production]] that needs to be started, it specifies the [[Product product]] to produce
   * and the [[Quantity quantity]] in which it needs to be produced.
   */
  case ToStart(ID: ProductionID, productToProduce: Product, quantityToProduce: Quantity)

  /**
   * A [[Production production]] that has already started, it specifies the [[Product product]] that is being produced
   * and the [[Quantity quantity]] in which it is being produced.
   */
  case InProgress(ID: ProductionID, productInProduction: Product, quantityInProduction: Quantity)

  /**
   * A [[Production production]] that ended, it has a [[LotNumber lot number]] and specified the [[Product product]]
   * that was produced and in which [[Quantity quantity]] it was produced.
   */
  case Ended(ID: ProductionID, lotNumber: LotNumber, producedProduct: Product, producedQuantity: Quantity)

/**
 * A lot number. TODO: ask domain experts how it can be obtained.
 */
final case class LotNumber()

/**
 * A list of [[RecipeLine ingredients and the respective quantity]] needed to produce a quintal of a product.
 */
final case class Recipe(lines: List[RecipeLine])

/**
 * A line of a [[Recipe recipe]] containing an [[Ingredient ingredient]] and the [[WeightInQuintals weight in quintals]]
 * of it needed by the recipe.
 */
final case class RecipeLine(ingredient: Ingredient, quintalsNeeded: WeightInQuintals)

/**
 * An ingredient that may be needed by a [[Recipe recipe]].
 */
enum Ingredient:
  case Milk
  case Cream
  case Rennet
  case Salt
  case Probiotics

/**
 * Associates to each [[CheeseType cheese type]] the [[Recipe recipe]] to produce a quintal of it.
 */
type RecipeBook = CheeseType => Recipe
