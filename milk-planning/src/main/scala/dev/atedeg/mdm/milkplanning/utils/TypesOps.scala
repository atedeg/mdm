package dev.atedeg.mdm.milkplanning.utils

import scala.annotation.targetName

import cats.kernel.Order
import eu.timepit.refined.predicates.all.NonNegative
import eu.timepit.refined.refineV

import dev.atedeg.mdm.milkplanning.types.{ Quantity, QuintalsOfMilk, StockedQuantity }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

object QuintalsOfMilkOps:

  given Order[QuintalsOfMilk] with

    override def compare(x: QuintalsOfMilk, y: QuintalsOfMilk): Int =
      Order[NonNegativeDecimal].compare(x.quintals, y.quintals)

  extension (qom1: QuintalsOfMilk)

    @targetName("plusQuintals")
    def +(qom2: QuintalsOfMilk): QuintalsOfMilk = QuintalsOfMilk(qom1.quintals plus qom2.quintals)

    @targetName("minusQuintals")
    def -(qom2: QuintalsOfMilk): QuintalsOfMilk = QuintalsOfMilk(qom1.quintals minus qom2.quintals)

    @targetName("timesQuintals")
    def **(nn: NonNegativeNumber): QuintalsOfMilk = QuintalsOfMilk(qom1.quintals * nn.toNonNegativeDecimal)
