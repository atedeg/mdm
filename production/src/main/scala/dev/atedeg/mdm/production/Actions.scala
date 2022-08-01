package dev.atedeg.mdm.production

import OutgoingEvent.*
import cats.Monad
import cats.syntax.all.*

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.monads.*

/**
 * Starts a [[Production.ToStart production]] by calculating the quintals of [[Ingredient ingredients]] needed to
 * produce the specified [[Product product]]; the [[Ingredient ingredients]] needed are specified
 * by the [[Recipe recipe]] read from a [[RecipeBook recipe book]].
 */
def startProduction[M[_]: Monad: CanRaise[MissingRecipe]: Emits[StartProduction]](recipeBook: RecipeBook)(
    production: Production.ToStart,
): M[Production.InProgress] =
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
def endProduction[M[_]: Monad: Emits[ProductionEnded]](production: Production.InProgress): M[Production.Ended] = for {
  lotNumber <- getLotNumber
  id = production.ID
  producedProduct = production.productInProduction
  unitsProduced = production.unitsInProduction
  _ <- emit(ProductionEnded(id, lotNumber): ProductionEnded)
} yield Production.Ended(id, lotNumber, producedProduct, unitsProduced)

def getLotNumber[M[_]: Monad]: M[LotNumber] = ???
