package dev.atedeg.mdm.utils

import scala.compiletime.constValue
import scala.language.implicitConversions

import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.numeric.Interval.Closed
import eu.timepit.refined.predicates.all.{ NonNegative, Positive }

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToNumberInRange[L <: Double & Singleton, U <: Double & Singleton](
    inline d: Double,
): DecimalInClosedRange[L, U] =
  inline if constValue[L] <= d && d <= constValue[U] then coerce(d) else compiletime.error("Not in the desired range")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToNumberInOpenClosedRange[L <: Double & Singleton, U <: Double & Singleton](
    inline d: Double,
): DecimalInOpenClosedRange[L, U] =
  inline if constValue[L] < d && d <= constValue[U] then coerce(d) else compiletime.error("Not in the desired range")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def intToPositiveNumber(inline i: Int): PositiveNumber =
  inline if i > 0 then coerce(i) else compiletime.error("Not a positive number")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def intToNonNegativeNumber(inline i: Int): NonNegativeNumber =
  inline if i >= 0 then coerce(i) else compiletime.error("Not a non-negative number")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToPositiveDecimal(inline d: Double): PositiveDecimal =
  inline if d > 0 then coerce(d) else compiletime.error("Not a positive decimal")

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.OptionPartial"))
inline implicit def doubleToNonNegativeDecimal(inline d: Double): NonNegativeDecimal =
  inline if d >= 0 then coerce(d) else compiletime.error("Not a non-negative decimal")
