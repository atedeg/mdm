package dev.atedeg.mdm.milkplanning.types

import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.given
import dev.atedeg.mdm.milkplanning.utils.given
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.{ emit, thenReturn, when, Emits }

/**
 * .
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
  val RequestedProduct(p @ Product(cheeseType, weight), quantity, _) = product
  val unitsToProduce = quantity.n.toNonNegative - stock(p).quantity
  val gramsToProduce = unitsToProduce * weight.n.toNonNegative
  val quintalsToProduce = gramsToProduce.toDecimal / 100_000
  val neededQuintals = recipeBook(cheeseType).n.toNonNegative * quintalsToProduce
  neededQuintals.ceil.quintalsOfMilk

private def magicAiEstimator(
    milkOfPreviousYear: QuintalsOfMilk,
    milkNeeded: QuintalsOfMilk,
    stockedMilk: QuintalsOfMilk,
): QuintalsOfMilk = max(milkOfPreviousYear, milkNeeded) - stockedMilk
