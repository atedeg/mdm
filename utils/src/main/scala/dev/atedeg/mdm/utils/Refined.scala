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
type DecimalInClosedRange[L, U] = Double Refined Interval.Closed[L, U]
type DecimalInOpenClosedRange[L, U] = Double Refined Interval.OpenClosed[L, U]
type NonNegativeNumber = Int Refined NonNegative
type NonNegativeDecimal = Double Refined NonNegative
type Percentage = DecimalInClosedRange[0.0, 1.0]

extension [N: Numeric](n: N Refined Positive) def toNonNegative: N Refined NonNegative = coerce(n.value)

extension (n: DecimalInClosedRange[0.0, 100.0]) def percent: Percentage = coerce(n.value / 100.0)

extension (n: Percentage) def inverted: Percentage = coerce(1.0 - n.value)

extension [N, P <: Positive | NonNegative: ValidFor[N]: ValidFor[Double]](n: N Refined P)(using N: Numeric[N])
  def toDecimal: Double Refined P = coerce(N.toDouble(n.value))

extension [P <: Positive | NonNegative: ValidFor[Double]](d: Double Refined P)
  def toNumber: NonNegativeNumber = coerce(d.value.toInt)

given Conversion[PositiveNumber, NonNegativeNumber] with
  override def apply(x: PositiveNumber): NonNegativeNumber = coerce(x.value)

given Conversion[PositiveDecimal, NonNegativeDecimal] with
  override def apply(x: PositiveDecimal): NonNegativeDecimal = coerce(x.value)

given [N, P <: Positive | NonNegative: ValidFor[N]](using C: Ceil[N]): Ceil[N Refined P] with
  override def toCeil(n: N Refined P): N Refined P = coerce(C.toCeil(n.value))

// `T Refined P` has an order relation if `T` has an order relation
given refinedOrd[N: Order, P]: Order[N Refined P] with
  override def compare(x: N Refined P, y: N Refined P): Int = Order[N].compare(x.value, y.value)

given refinedOrdering[N: Ordering, P]: Ordering[N Refined P] with
  override def compare(x: N Refined P, y: N Refined P): Int = Ordering[N].compare(x.value, y.value)

// `T Refined P` has an equality operation if `T` has an equality operation
given refinedEq[N: Eq, P]: Eq[N Refined P] with
  override def eqv(x: N Refined P, y: N Refined P): Boolean = Eq[N].eqv(x.value, y.value)

// Instances for the various numeric ops

extension [N](n: N) def refined[P: ValidFor[N]]: Either[String, N Refined P] = refineV[P](n)

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
def coerce[A, P](a: A)(using Validate[A, P]): A Refined P = a.refined.toOption.get

private type ValidFor[N] = [P] =>> Validate[N, P]

given refinedPlus[N, P <: Positive | NonNegative: ValidFor[N]](using Op: Plus[N]): Plus[N Refined P] with
  override def plus(x: N Refined P, y: N Refined P): N Refined P = coerce(Op.plus(x.value, y.value))

given refinedTimes[N, P <: Positive | NonNegative: ValidFor[N]](using Op: Times[N]): Times[N Refined P] with
  override def times(x: N Refined P, y: N Refined P): N Refined P = coerce(Op.times(x.value, y.value))

given refinedTimesPercentage: Times[Percentage] with
  override def times(x: Percentage, y: Percentage): Percentage = coerce(x.value * y.value)

given refinedDivFloat[P <: Positive | NonNegative: ValidFor[Double]]: Div[Double Refined P] with

  override def div(x: Double Refined P, y: Double Refined P): Double Refined P =
    coerce(summon[Div[Double]].div(x.value, y.value))

given refinedDivInt[P <: NonNegative: ValidFor[Int]]: Div[Int Refined P] with

  override def div(x: Int Refined P, y: Int Refined P): Int Refined P =
    coerce(if y.value > x.value then 0 else summon[Div[Int]].div(x.value, y.value))

given refinedMinus[N: Numeric, P <: NonNegative: ValidFor[N]](using Op: Minus[N]): Minus[N Refined P] with

  override def minus(x: N Refined P, y: N Refined P): N Refined P =
    coerce(if y.value > x.value then Numeric[N].zero else Op.minus(x.value, y.value))

given refinedDistance[N, P <: NonNegative: ValidFor[N]](using D: Distance[N]): Distance[N Refined P] with

  override def distance(x: N Refined P, y: N Refined P): N Refined P = coerce(D.distance(x.value, y.value))
