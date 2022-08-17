package dev.atedeg.mdm.pricing.utils

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

given discountPercentageTimes(using
    OpTimes: Times[Double],
    OpMinus: Minus[Double],
): Times[DecimalInOpenClosedRange[0.0, 1.0]] with
  override def times(
      x: DecimalInOpenClosedRange[0.0, 1.0],
      y: DecimalInOpenClosedRange[0.0, 1.0],
  ): DecimalInOpenClosedRange[0.0, 1.0] =
    coerce(OpMinus.minus(1.0, OpTimes.times(OpMinus.minus(1.0, x.value), OpMinus.minus(1.0, y.value))))
