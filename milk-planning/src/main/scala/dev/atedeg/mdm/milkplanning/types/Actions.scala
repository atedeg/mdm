package dev.atedeg.mdm.milkplanning.types

import cats.Monad

import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.utils.*
import dev.atedeg.mdm.utils.{ emit, thenReturn, Emits }

def estimateQuintalsOfMilk[M[_]: Emits[OrderMilk]: Monad](
    milkOfThePreviousYear: QuintalsOfMilk,
    milkNeededByProducts: QuintalsOfMilk,
    currentStock: Stock,
    stockedMilk: QuintalsOfMilk,
): M[QuintalsOfMilk] = {
  val estimation = milkOfThePreviousYear + milkNeededByProducts - stockedMilk
  emit[M, OrderMilk](OrderMilk(estimation)) thenReturn estimation
}
