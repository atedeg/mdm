package dev.atedeg.mdm.production

import java.time.{ LocalDate, LocalDateTime }
import java.util.UUID

import OutgoingEvent.*
import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.production.utils.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Prepares the new [[Production.ToStart productions to start]] from a given
 * [[ProductionPlan production plan]].
 */
def setupProductions(productionPlan: ProductionPlan): NonEmptyList[Production.ToStart] =
  productionPlan.plan.map(item => Production.ToStart(generateProductionId, item.productToProduce, item.units))

private def generateProductionId: ProductionID = ProductionID(UUID.randomUUID)

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
  for
    recipe <- recipeBook.recipeBook.get(typeToProduce) ifMissingRaise MissingRecipe(typeToProduce)
    quintalsToProduce = (production.unitsToProduce.n * gramsOfSingleUnit.n).toDecimal / 100_000
    neededIngredients = recipe.lines.map(_ * quintalsToProduce)
    _ <- emit(StartProduction(neededIngredients): StartProduction)
  yield Production.InProgress(production.ID, production.productToProduce, production.unitsToProduce)

/**
 * Ends a [[Production.InProgress production]] by assigning it a [[BatchID batch ID]].
 */
def endProduction[M[_]: Monad: Emits[NewBatch]](ripeningDays: CheeseTypeRipeningDays)(
    production: Production.InProgress,
): M[Production.Ended] =
  val batchID = generateBatchID
  val producedProduct = production.productInProduction
  val unitsProduced = production.unitsInProduction
  val cheeseType = production.productInProduction.cheeseType
  val days = ripeningDays.value(cheeseType).days.value.toLong
  val readyBy = LocalDate.now.plusDays(days)
  emit(NewBatch(batchID, cheeseType, readyBy): NewBatch)
    .thenReturn(Production.Ended(production.ID, batchID, producedProduct, unitsProduced))

private def generateBatchID: BatchID = BatchID(UUID.randomUUID)
