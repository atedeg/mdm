package dev.atedeg.mdm.utils

import scala.annotation.targetName
import scala.math.Numeric.Implicits.*
import scala.math.Ordering.Implicits.*

final case class Range[T: Ordering] private[utils] (min: T, max: T)

final case class RangePercentage private[utils] (percentage: Double)

extension [T: Numeric](x: T)

  def percent: RangePercentage = RangePercentage(x.toDouble / 100)

  @targetName("plusMinus")
  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  def +-(y: T): Range[T] = Range(x - y, x + y)

  @targetName("plusMinusPercent")
  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  def +-(y: RangePercentage): Range[Double] =
    Range(x.toDouble * (1.0 - y.percentage), x.toDouble * (1.0 + y.percentage))

extension [T: Ordering](x: T)

  def isInRange(range: Range[T]): Boolean =
    x >= range.min && x <= range.max
