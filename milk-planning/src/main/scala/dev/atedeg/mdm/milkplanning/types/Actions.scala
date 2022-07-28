package dev.atedeg.mdm.milkplanning.types

import cats.Monad
import cats.data.NonEmptyList
import eu.timepit.refined.auto.autoUnwrap

import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.given
import dev.atedeg.mdm.milkplanning.utils.given
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.{ emit, thenReturn, when, Emits }

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
    book: RecipeBook,
): QuintalsOfMilk =
  requestedProducts
    .map(p => (p.product, p.quantity))
    .map { case (prod, quantity) => (prod, quantity.n - stock(prod).quantity) }
    .map { case (prod, quantity) => book(prod.cheeseType).quintals * quantity }
    .map(_.toQuintalsOfMilk)
    .reduce(_ + _)

private def magicAiEstimator(
    milkOfPreviousYear: QuintalsOfMilk,
    milkNeeded: QuintalsOfMilk,
    stockedMilk: QuintalsOfMilk,
): QuintalsOfMilk = max(milkOfPreviousYear, milkNeeded) - stockedMilk
