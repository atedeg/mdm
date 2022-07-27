package dev.atedeg.mdm.milkplanning.utils

import scala.annotation.targetName

import cats.kernel.Order
import eu.timepit.refined.predicates.all.NonNegative
import eu.timepit.refined.refineV

import dev.atedeg.mdm.milkplanning.types.QuintalsOfMilk
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

object QuintalsOfMilkOps:

  given Order[QuintalsOfMilk] with
    override def compare(x: QuintalsOfMilk, y: QuintalsOfMilk): Int = Order[NonNegativeDecimal].compare(x.n, y.n)

  extension (qom1: QuintalsOfMilk)

    @targetName("plus")
    def +(qom2: QuintalsOfMilk): QuintalsOfMilk = QuintalsOfMilk(qom1.n plus qom2.n)

    @targetName("minus")
    def -(qom2: QuintalsOfMilk): QuintalsOfMilk = QuintalsOfMilk(qom1.n minus qom2.n)

  extension (d: Double)

    @SuppressWarnings(Array("org.wartremover.warts.OptionPartial")) // FIXME: try with a macro
    def quintalsOfMilk: QuintalsOfMilk = QuintalsOfMilk(refineV[NonNegative](d).toOption.get)
