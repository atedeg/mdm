package dev.atedeg.mdm.milkplanning

import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.milkplanning.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.given
import dev.atedeg.mdm.milkplanning.utils.given
import dev.atedeg.mdm.products.{ Grams, Product }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.{ emit, thenReturn, when, Emits }

/**
 * Estimate the amount of milk needed for the following week's production.
 * The estimate takes into account the milk processed in the same week last year,
 * the products ordered for the following week, the current [[Stock stock]] and the [[QuintalsOfMilk quintals of milk]]
 * currently in stock and return the [[QuintalsOfMilk quintals of milk]] needed for the following week.
 */
def estimateQuintalsOfMilk[M[_]: Emits[OrderMilk]: Monad](
    milkOfPreviousYear: QuintalsOfMilk,
    requestedProductsForWeek: NonEmptyList[RequestedProduct],
    currentStock: Stock,
    recipeBook: RecipeBook,
    stockedMilk: QuintalsOfMilk,
): M[QuintalsOfMilk] =
  val milkNeeded = milkNeededForProducts(requestedProductsForWeek, currentStock, recipeBook)
  val estimatedMilk = magicAiEstimator(milkOfPreviousYear, milkNeeded, stockedMilk)
  when(estimatedMilk.quintals > 0)(emit(OrderMilk(estimatedMilk): OrderMilk)).thenReturn(estimatedMilk)

private def milkNeededForProducts(
    requestedProducts: NonEmptyList[RequestedProduct],
    stock: Stock,
    recipeBook: RecipeBook,
): QuintalsOfMilk =
  requestedProducts
    .map(milkNeededForProduct(_, stock, recipeBook))
    .foldLeft(0.quintalsOfMilk)(_ + _)

@SuppressWarnings(Array("scalafix:DisableSyntax.noValPatterns"))
private def milkNeededForProduct(
    product: RequestedProduct,
    stock: Stock,
    recipeBook: RecipeBook,
): QuintalsOfMilk =
  val RequestedProduct(p @ Product(cheeseType, Grams(weight)), Quantity(quantity), _) = product
  val unitsToProduce = quantity.toNonNegative - stock(p).quantity
  val gramsToProduce = unitsToProduce * weight
  val quintalsToProduce = gramsToProduce.toDecimal / 100_000
  val neededQuintals = quintalsToProduce * recipeBook(cheeseType).n
  neededQuintals.ceil.quintalsOfMilk

private def magicAiEstimator(
    milkOfPreviousYear: QuintalsOfMilk,
    milkNeeded: QuintalsOfMilk,
    stockedMilk: QuintalsOfMilk,
): QuintalsOfMilk = max(milkOfPreviousYear, milkNeeded) - stockedMilk
