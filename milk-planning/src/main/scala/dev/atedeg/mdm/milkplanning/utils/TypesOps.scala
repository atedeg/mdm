package dev.atedeg.mdm.milkplanning.utils

import scala.annotation.targetName

import dev.atedeg.mdm.milkplanning.types.QuintalsOfMilk

extension (qom1: QuintalsOfMilk)

  @targetName("plus")
  def +(qom2: QuintalsOfMilk): QuintalsOfMilk = ???

  @targetName("minus")
  def -(qom2: QuintalsOfMilk): Option[QuintalsOfMilk] = ???
