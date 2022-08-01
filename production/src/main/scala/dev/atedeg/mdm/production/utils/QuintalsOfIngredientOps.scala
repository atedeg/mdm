package dev.atedeg.mdm.production.utils

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

import scala.annotation.targetName

extension (q: QuintalsOfIngredient)
  @targetName("quintalsOfIngredientTimesDecimal")
  def *(n: PositiveDecimal) = QuintalsOfIngredient(q.quintals * n.quintals, q.ingredient)

extension (n: PositiveDecimal)
  def quintals : WeightInQuintals = WeightInQuintals(n)