package dev.atedeg.mdm.production

import cats.Monad
import cats.syntax.all.*
import dev.atedeg.mdm.utils.*
import OutgoingEvent.*

// TODO: Does this action emit a "startProduction" event that is used by the machines? Or does it
//       communicate in a different way with the smart machines in order to start the production?
// TODO: Also maybe a better event would be a StartProduction event with the quintals of ingredients
//       needed and the production ID (instead of many small events that are harder to keep together)!!!
//       This event would represent the signal for the machines to assemble the materials and start the
//       production process.
/**
 * Starts a [[Production.ToStart production]] by calculating the quintals of [[Ingredient ingredients]] needed to
 * produce the specified [[Product product]]; the [[Ingredient ingredients]] needed are specified
 * by the [[Recipe recipe]] read from a [[RecipeBook recipe book]].
 */
def startProduction[M[_]: Monad: CanRaise[MissingRecipe]: Emits[StartProduction]]
  (recipeBook: RecipeBook)
  (production: Production.ToStart)
  : M[Production.InProgress] =
    val typeToProduce = production.productToProduce.cheeseType
    val gramsOfSingleUnit = production.productToProduce.weight
    for {
      recipe <- recipeBook(typeToProduce) ifMissingRaise MissingRecipe(typeToProduce)
      quintalsToProduce = (production.unitsToProduce * gramsOfSingleUnit).toQuintals
      neededIngredients = recipe.lines.map(_ * quintalsToProduce)
      _ <- emit(StartProduction(neededIngredients): StartProduction)
    } yield Production.InProgress(production.ID, production.productToProduce, production.unitsToProduce)

/**
 * Ends a [[Production.InProgress production]] by assigning it a [[LotNumber lot number]].
 */
def endProduction[M[_]: Monad: Emits[ProductionEnded]]
  (production: Production.InProgress): M[Production.Ended] = for {
    lotNumber: LotNumber <- getLotNumber
    id = production.ID
    producedProduct = production.productInProduction
    unitsProduced = production.unitsInProduction
    _ <- emit(ProductionEnded(id, lotNumber): ProductionEnded)
  } yield Production.Ended(id, lotNumber, producedProduct, unitsProduced)

def getLotNumber[M[_]: Monad]: M[LotNumber] = ???