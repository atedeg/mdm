package dev.atedeg.mdm.utils

import scala.annotation.targetName

import cats.kernel.Order
import eu.timepit.refined.{ refineT, refineV }
import eu.timepit.refined.api.{ Refined, Validate }
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.numeric.{ Interval, NonNegative, Positive }
import Coercion.*

type PositiveNumber = Int Refined Positive
type PositiveDecimal = Double Refined Positive
type NumberInClosedRange[L, U] = Int Refined Interval.Closed[L, U]
type NonNegativeNumber = Int Refined NonNegative
type NonNegativeDecimal = Double Refined NonNegative

private object Coercion:
  extension [A](a: A)
    @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
    def coerceTo[P](using Validate[A, P]): A Refined P = refineV[P](a).toOption.get

// `T Refined P` has an order relation if `T` has an order relation
given refinedOrd[N, P](using O: Order[N]): Order[N Refined P] with
  override def compare(x: N Refined P, y: N Refined P): Int = O.compare(x, y)

