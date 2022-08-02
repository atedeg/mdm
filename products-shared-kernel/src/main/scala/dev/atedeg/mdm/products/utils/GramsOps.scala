package dev.atedeg.mdm.products.utils

import cats.kernel.Order
import eu.timepit.refined.predicates.all.Positive

import dev.atedeg.mdm.products.Grams
import dev.atedeg.mdm.utils.{ coerce, PositiveNumber }

private[products] def coerceToGrams(n: Int): Grams = Grams(coerce(n))

extension (n: PositiveNumber) def grams: Grams = Grams(n)

given Order[Grams] with
  def compare(x: Grams, y: Grams): Int = Order[Int].compare(x.n.value, y.n.value)
