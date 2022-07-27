package dev.atedeg.mdm.milkplanning.types

import cats.{ Monad, Order }
import cats.syntax.all.*
import eu.timepit.refined.auto.autoUnwrap

import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.given
import dev.atedeg.mdm.utils.{ emit, max, thenReturn, when, Emits, given }

def estimateQuintalsOfMilk[M[_]: Emits[OrderMilk]: Monad](
    milkOfPreviousYear: QuintalsOfMilk,
    requestedProductsForWeek: List[RequestedProduct],
    currentStock: Stock,
    recipeBook: RecipeBook,
    stockedMilk: QuintalsOfMilk,
): M[QuintalsOfMilk] =
  val milkNeeded = milkNeededForProducts(requestedProductsForWeek, currentStock, recipeBook)
  val estimatedMilk = magicAiEstimator(milkOfPreviousYear, milkNeeded, stockedMilk)
  when(estimatedMilk.quintals > 0)(emit(OrderMilk(estimatedMilk): OrderMilk)).thenReturn(estimatedMilk)

private def milkNeededForProducts(value: List[RequestedProduct], stock: Stock, book: RecipeBook): QuintalsOfMilk = ???

private def magicAiEstimator(
    milkOfPreviousYear: QuintalsOfMilk,
    milkNeeded: QuintalsOfMilk,
    stockedMilk: QuintalsOfMilk,
): QuintalsOfMilk = max(milkOfPreviousYear, milkNeeded) - stockedMilk
