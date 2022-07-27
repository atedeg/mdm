package dev.atedeg.mdm.utils

import scala.annotation.targetName

import cats.kernel.Order
import eu.timepit.refined.{ refineT, refineV }
import eu.timepit.refined.api.{ Refined, Validate }
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.numeric.{ Interval, NonNegative, Positive }

type PositiveNumber = Int Refined Positive
type PositiveDecimal = Double Refined Positive
type NumberInClosedRange[L, U] = Int Refined Interval.Closed[L, U]
type NonNegativeNumber = Int Refined NonNegative
type NonNegativeDecimal = Double Refined NonNegative

private object Conversion:

  extension [A](a: A) def coerceTo[P](using Validate[A, P]): A Refined P = refineV[P](a).toOption.coerce

  extension [T](o: Option[T])

    @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
    def coerce: T = o.get

import Conversion.*

extension (p1: PositiveNumber)

  @targetName("plusPositiveNumber")
  def +(p2: PositiveNumber): PositiveNumber = (p1.value + p2.value).coerceTo[Positive]
  def plus(p2: PositiveNumber): PositiveNumber = p1 + p2

  @targetName("timesPositiveNumber")
  def *(p2: PositiveNumber): PositiveNumber = (p1.value * p2.value).coerceTo[Positive]
  def times(p2: PositiveNumber): PositiveNumber = p1 * p2

  @targetName("minusPositiveNumber")
  def -(p2: PositiveNumber): NonNegativeNumber = (if p1 < p2 then 0 else p1.value - p2.value).coerceTo[NonNegative]
  def minus(p2: PositiveNumber): NonNegativeNumber = p1 - p2

  @targetName("dividePositiveNumber")
  def /(p2: PositiveNumber): PositiveDecimal = (p1.value / p2.value.toDouble).coerceTo[Positive]
  def divide(p2: PositiveNumber): PositiveDecimal = p1 / p2
  def toPositiveDecimal: PositiveDecimal = p1.value.toDouble.coerceTo[Positive]
  def toNonNegativeNumber: NonNegativeNumber = p1.value.coerceTo[NonNegative]

extension (p1: PositiveDecimal)

  @targetName("plusPositiveDecimal")
  def +(p2: PositiveDecimal): PositiveDecimal = (p1.value + p2.value).coerceTo[Positive]
  def plus(p2: PositiveDecimal): PositiveDecimal = p1 + p2

  @targetName("timesPositiveDecimal")
  def *(p2: PositiveDecimal): PositiveDecimal = (p1.value * p2.value).coerceTo[Positive]
  def times(p2: PositiveDecimal): PositiveDecimal = p1 * p2

  @targetName("minusPositiveDecimal")
  def -(p2: PositiveDecimal): NonNegativeDecimal = (if p1 < p2 then 0 else p1.value - p2.value).coerceTo[NonNegative]
  def minus(p2: PositiveDecimal): NonNegativeDecimal = p1 - p2

  @targetName("dividePositiveDecimal")
  def /(p2: PositiveDecimal): PositiveDecimal = (p1.value / p2.value).coerceTo[Positive]
  def divide(p2: PositiveDecimal): PositiveDecimal = p1 / p2
  def toNonNegativeDecimal: NonNegativeDecimal = p1.value.coerceTo[NonNegative]

extension (p1: NonNegativeNumber)

  @targetName("plusNonNegativeNumberOperator")
  def +(p2: NonNegativeNumber): NonNegativeNumber = (p1.value + p2.value).coerceTo[NonNegative]

  @targetName("plusNonNegativeNumber")
  def plus(p2: NonNegativeNumber): NonNegativeNumber = p1 + p2

  @targetName("timesNonNegativeNumberOperator")
  def *(p2: NonNegativeNumber): NonNegativeNumber = (p1.value * p2.value).coerceTo[NonNegative]

  @targetName("timesNonNegativeNumber")
  def times(p2: NonNegativeNumber): NonNegativeNumber = p1 * p2

  @targetName("minusNonNegativeNumberOperator")
  def -(p2: NonNegativeNumber): NonNegativeNumber = (if p1 < p2 then 0 else p1.value - p2.value).coerceTo[NonNegative]

  @targetName("minusNonNegativeNumber")
  def minus(p2: NonNegativeNumber): NonNegativeNumber = p1 - p2

  @targetName("divideNonNegativeNumberOperator")
  def /(p2: NonNegativeNumber): NonNegativeDecimal = (p1.value / p2.value.toDouble).coerceTo[NonNegative]

  @targetName("divideNonNegativeNumber")
  def divide(p2: NonNegativeNumber): NonNegativeDecimal = p1 / p2

  def toNonNegativeDecimal: NonNegativeDecimal = p1.value.toDouble.coerceTo[NonNegative]

extension (p1: NonNegativeDecimal)

  @targetName("plusNonNegativeDecimalOperator")
  def +(p2: NonNegativeDecimal): NonNegativeDecimal = (p1.value + p2.value).coerceTo[NonNegative]

  @targetName("plusNonNegativeDecimal")
  def plus(p2: NonNegativeDecimal): NonNegativeDecimal = p1 + p2

  @targetName("timesNonNegativeDecimalOperator")
  def *(p2: NonNegativeDecimal): NonNegativeDecimal = (p1.value * p2.value).coerceTo[NonNegative]

  @targetName("timesNonNegativeDecimal")
  def times(p2: NonNegativeDecimal): NonNegativeDecimal = p1 * p2

  @targetName("minusNonNegativeDecimalOperator")
  def -(p2: NonNegativeDecimal): NonNegativeDecimal = (if p1 < p2 then 0 else p1.value - p2.value).coerceTo[NonNegative]

  @targetName("minusNonNegativeDecimal")
  def minus(p2: NonNegativeDecimal): NonNegativeDecimal = p1 - p2

  @targetName("divideNonNegativeDecimalOperator")
  def /(p2: NonNegativeDecimal): NonNegativeDecimal = (p1.value / p2.value).coerceTo[NonNegative]

  @targetName("divideNonNegativeDecimal")
  def divide(p2: NonNegativeDecimal): NonNegativeDecimal = p1 / p2

given Order[PositiveNumber] with
  override def compare(x: PositiveNumber, y: PositiveNumber): Int = Order[Int].compare(x, y)

given Order[PositiveDecimal] with
  override def compare(x: PositiveDecimal, y: PositiveDecimal): Int = Order[Double].compare(x, y)

given Order[NonNegativeNumber] with
  override def compare(x: NonNegativeNumber, y: NonNegativeNumber): Int = Order[Int].compare(x, y)

given Order[NonNegativeDecimal] with
  override def compare(x: NonNegativeDecimal, y: NonNegativeDecimal): Int = Order[Double].compare(x, y)
