package dev.atedeg.mdm.pricing.utils

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

given discountPercentageTimes: Times[DecimalInOpenClosedRange[0.0, 1.0]] with
  override def times(
      x: DecimalInOpenClosedRange[0.0, 1.0],
      y: DecimalInOpenClosedRange[0.0, 1.0],
  ): DecimalInOpenClosedRange[0.0, 1.0] =
    coerce(1.0 - (1.0 - x.value) * (1.0 - y.value))
