package dev.atedeg.mdm.production.dto

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps

final case class StartProductionDTO(neededIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
final case class ProductionEndedDTO(productionID: String, batchID: String)
final case class ProductionPlanReadyDTO(productionPlan: List[ProductionPlanItemDTO])
final case class ProductionPlanItemDTO(product: ProductDTO, units: Int)

object StartProductionDTO:
  given DTO[StartProduction, StartProductionDTO] = interCaseClassDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO

object ProductionEndedDTO:
  given DTO[ProductionEnded, ProductionEndedDTO] = interCaseClassDTO
  private given DTO[ProductionID, String] = caseClassDTO
  private given DTO[BatchID, String] = caseClassDTO

object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = interCaseClassDTO
  private given DTO[ProductionPlan, List[ProductionPlanItemDTO]] = caseClassDTO
  private given DTO[ProductionPlanItem, ProductionPlanItemDTO] = interCaseClassDTO
  private given DTO[NumberOfUnits, Int] = caseClassDTO
