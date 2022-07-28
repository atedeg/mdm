package dev.atedeg.mdm.utils

import scala.annotation.targetName

import cats.kernel.{ Eq, Order }
import cats.syntax.all.*
import eu.timepit.refined.api.{ Refined, Validate }
import eu.timepit.refined.numeric.{ Interval, NonNegative, Positive }
import eu.timepit.refined.refineV
import math.Ordering.Implicits.infixOrderingOps

type PositiveNumber = Int Refined Positive
type PositiveDecimal = Double Refined Positive
type NumberInClosedRange[L, U] = Int Refined Interval.Closed[L, U]
type NonNegativeNumber = Int Refined NonNegative
type NonNegativeDecimal = Double Refined NonNegative
private type ValidFor[N] = [P] =>> Validate[N, P]

// `T Refined P` has an order relation if `T` has an order relation
given refinedOrd[N: Order, P]: Order[N Refined P] with
  override def compare(x: N Refined P, y: N Refined P): Int = Order[N].compare(x.value, y.value)

// `T Refined P` has an equality operation if `T` has an equality operation
given refinedEq[N: Eq, P]: Eq[N Refined P] with
  override def eqv(x: N Refined P, y: N Refined P): Boolean = Eq[N].eqv(x.value, y.value)

// Instances for the various numeric ops
@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
private def coerce[A, P](a: A)(using Validate[A, P]): A Refined P = refineV[P](a).toOption.get

given refinedPlus[N, P <: Positive | NonNegative: ValidFor[N]](using Op: Plus[N]): Plus[N Refined P] with
  override def plus(x: N Refined P, y: N Refined P): N Refined P = coerce(Op.plus(x.value, y.value))

given refinedTimes[N, P <: Positive | NonNegative: ValidFor[N]](using Op: Times[N]): Times[N Refined P] with
  override def times(x: N Refined P, y: N Refined P): N Refined P = coerce(Op.times(x.value, y.value))

given refinedDiv[N: Numeric, P <: NonNegative: ValidFor[N]](using Op: Div[N]): Div[N Refined P] with

  override def div(x: N Refined P, y: N Refined P): N Refined P =
    coerce(if y.value > x.value then Numeric[N].zero else Op.div(x.value, y.value))

given refinedMinus[N: Numeric, P <: NonNegative: ValidFor[N]](using Op: Minus[N]): Minus[N Refined P] with

  override def minus(x: N Refined P, y: N Refined P): N Refined P =
    coerce(if y.value > x.value then Numeric[N].zero else Op.minus(x.value, y.value))
