package dev.atedeg.mdm.production

import cats.Monad
import cats.syntax.all.*
import dev.atedeg.mdm.utils.*
import OutgoingEvent.*

// TODO: Does this action emits a "startProduction" event that is used by the machines? Or does it
//       communicate in a different way with the smart machines in order to start the production?
/**
 * Starts a [[Production.ToStart production]] by calculating the quintals of [[Ingredient ingredients]] needed to
 * produce the specified [[Product product]]; the [[Ingredient ingredients]] needed are specified
 * by the [[Recipe recipe]] read from a [[RecipeBook recipe book]].
 */
def startProduction[M[_]: Monad: CanRaise[MissingRecipe]: Emits[IngredientUsed]]
  (recipeBook: RecipeBook)
  (production: Production.ToStart)
  : M[Production.InProgress] =
    val typeToProduce = production.productToProduce.cheeseType
    val gramsOfSingleUnit = production.productToProduce.weight
    for {
      recipe <- recipeBook(typeToProduce) ifMissingRaise MissingRecipe(typeToProduce)
      quintalsToProduce = production.unitsToProduce * gramsOfSingleUnit//.toQuintals
      neededIngredients = recipe.lines.map(l => (l.ingredient, l.quintalsNeeded * quintalsToProduce))
      _ <- neededIngredients.forEachDo((i, w) => emit(IngredientUsed(i, w): IngredientUsed))
    } yield Production.InProgress(production.ID, production.productToProduce, production.unitsToProduce)

/**
 * Ends a [[Production.InProgress production]] by assigning it a [[LotNumber lot number]].
 */
def endProduction[M[_]: Monad: Emits[ProductionEnded]]
  (production: Production.InProgress): Production.Ended = ???