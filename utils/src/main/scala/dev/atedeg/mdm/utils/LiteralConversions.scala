package dev.atedeg.mdm.utils

import scala.compiletime.{ codeOf, constValue }
import scala.language.implicitConversions
import scala.quoted.{ Expr, Quotes }

import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.{ NonNegative, Positive }
import eu.timepit.refined.refineV

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def intToNumberInRange[L <: Int & Singleton, U <: Int & Singleton](
                                                                                    inline i: Int,
                                                                                  ): NumberInClosedRange[L, U] =
  inline if constValue[L] <= i && i <= constValue[U]
  then refineV[Interval.Closed[L, U]](i).toOption.get
  else compiletime.error("Not in the desired range")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def intToPositiveNumber(inline i: Int): PositiveNumber =
  inline if i > 0
  then refineV[Positive](i).toOption.get
  else compiletime.error("Not a positive number")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def intToNonNegativeNumber(inline i: Int): NonNegativeNumber =
  inline if i >= 0
  then refineV[NonNegative](i).toOption.get
  else compiletime.error("Not a non-negative number")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToPositiveDecimal(inline d: Double): PositiveDecimal =
  inline if d > 0
  then refineV[Positive](d).toOption.get
  else compiletime.error("Not a positive decimal")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToNonNegativeDecimal(inline d: Double): NonNegativeDecimal =
  inline if d >= 0
  then refineV[NonNegative](d).toOption.get
  else compiletime.error("Not a non-negative decimal")