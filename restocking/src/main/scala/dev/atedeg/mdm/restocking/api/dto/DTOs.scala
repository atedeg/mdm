package dev.atedeg.mdm.restocking.api.dto

import dev.atedeg.mdm.restocking.QuintalsOfMilk
import dev.atedeg.mdm.restocking.api.RemainingMilk
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*

final case class RemainingMilkDTO(quintalsOfMilk: Int)

object RemainingMilkDTO:
  given DTO[RemainingMilk, RemainingMilkDTO] = productTypeDTO
  private given DTO[QuintalsOfMilk, Int] = unwrapFieldDTO
