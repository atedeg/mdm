package dev.atedeg.mdm.milkplanning.dsl

import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.refineV

import dev.atedeg.mdm.milkplanning.types.{ QuintalsOfMilk, StockedQuantity }

extension [A](x: Option[A])

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def coerce: A = x.get

extension (n: Double) def quintalsOfMilk: QuintalsOfMilk = QuintalsOfMilk(refineV[NonNegative](n).toOption.coerce)
extension (n: Int) def stockedQuantity: StockedQuantity = StockedQuantity(refineV[NonNegative](n).toOption.coerce)
