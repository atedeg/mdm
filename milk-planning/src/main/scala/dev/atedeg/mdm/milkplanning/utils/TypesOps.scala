package dev.atedeg.mdm.milkplanning.utils

import scala.annotation.targetName

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{ NonNegative, Positive }
import eu.timepit.refined.refineV

import dev.atedeg.mdm.milkplanning.types.QuintalsOfMilk
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.{ NonNegativeDecimal, PositiveDecimal }
import dev.atedeg.mdm.utils.given

extension (qom1: QuintalsOfMilk)

  @targetName("plus")
  def +(qom2: QuintalsOfMilk): QuintalsOfMilk = {
    val result: Option[NonNegativeDecimal] = qom1.n.value + qom2.n.value
    result match
      case Some(value) => QuintalsOfMilk(value)
      case _ => ??? // TODO: how can we handle this case?
  }

  @targetName("minus")
  def -(qom2: QuintalsOfMilk): QuintalsOfMilk = {
    val result: Option[NonNegativeDecimal] = qom1.n.value - qom2.n.value
    result match
      case Some(value) => QuintalsOfMilk(value)
      case _ => QuintalsOfMilk(0.0.nonNegativeDecimal)
  }
