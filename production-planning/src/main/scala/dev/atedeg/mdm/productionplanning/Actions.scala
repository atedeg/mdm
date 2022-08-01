package dev.atedeg.mdm.productionplanning

import cats.Monad

import dev.atedeg.mdm.productionplanning.{ CheeseTypeRipeningDays, RipeningDays }
import dev.atedeg.mdm.productionplanning.OutgoingEvent.ProductionPlanReady
import dev.atedeg.mdm.products.CheeseType
import dev.atedeg.mdm.utils.monads.Emits

private def createProductionPlan[M[_]: Emits[ProductionPlanReady]: Monad]: M[ProductionPlan] = ???

private def daysNeededForRipening(cheeseType: CheeseType, cheeseRipeningDays: CheeseTypeRipeningDays): RipeningDays =
  ???
