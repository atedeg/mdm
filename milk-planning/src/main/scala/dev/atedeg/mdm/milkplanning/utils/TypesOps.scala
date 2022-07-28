package dev.atedeg.mdm.milkplanning.utils

import scala.annotation.targetName

import cats.kernel.Order
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.predicates.all.NonNegative
import eu.timepit.refined.refineV

import dev.atedeg.mdm.milkplanning.types.{ Quantity, QuintalsOfMilk, StockedQuantity }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.NonNegativeNumber

object QuintalsOfMilkOps:

  given Order[QuintalsOfMilk] with
    override def compare(x: QuintalsOfMilk, y: QuintalsOfMilk): Int = Order[Int].compare(x.quintals, y.quintals)

  extension (n: NonNegativeNumber) def toQuintalsOfMilk: QuintalsOfMilk = QuintalsOfMilk(n)
