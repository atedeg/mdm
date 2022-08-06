package dev.atedeg.mdm.restocking.dto

import dev.atedeg.mdm.products.utils.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class OrderMilkDTO(quintalsOfMilk: Int)
final case class ProductionStartedDTO(quintalsOfIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = interCaseClassDTO
  private given DTO[QuintalsOfMilk, Int] = caseClassDTO

object ProductionStartedDTO:
  given DTO[ProductionStarted, ProductionStartedDTO] = interCaseClassDTO

object QuintalsOfIngredientDTO:
  given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO
