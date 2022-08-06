package dev.atedeg.mdm.restocking.dto

import dev.atedeg.mdm.products.utils.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.utils.serialization.DTO

final case class OrderMilkDTO(quintalsOfMilk: Int)
final case class ProductionStartedDTO(quintalsOfIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = DTO.interCaseClassDTO
  private given DTO[QuintalsOfMilk, Int] = DTO.caseClassDTO

object ProductionStartedDTO:
  given DTO[ProductionStarted, ProductionStartedDTO] = DTO.interCaseClassDTO

object QuintalsOfIngredientDTO:
  given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = DTO.interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = DTO.caseClassDTO
