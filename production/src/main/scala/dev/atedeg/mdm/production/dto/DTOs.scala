package dev.atedeg.mdm.production.dto

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps

final case class StartProductionDTO(neededIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
object StartProductionDTO:
  given DTO[StartProduction, StartProductionDTO] = interCaseClassDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO

final case class ProductionEndedDTO(productionID: String, batchID: String)
object ProductionEndedDTO:
  given DTO[ProductionEnded, ProductionEndedDTO] = interCaseClassDTO
  private given DTO[ProductionID, String] = caseClassDTO
  private given DTO[BatchID, String] = caseClassDTO

final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
final case class ProductToProduceDTO(product: ProductDTO, units: Int)
object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = interCaseClassDTO
  private given DTO[ProductionPlan, ProductionPlanDTO] = interCaseClassDTO
  private given DTO[ProductionPlanItem, ProductToProduceDTO] = interCaseClassDTO
  private given DTO[NumberOfUnits, Int] = caseClassDTO

final case class RecipeBookDTO(recipeBook: Map[String, RecipeDTO])
final case class RecipeDTO(recipe: List[QuintalsOfIngredientDTO])
object RecipeBookDTO:
  given DTO[RecipeBook, RecipeBookDTO] = interCaseClassDTO
  private given DTO[Recipe, RecipeDTO] = interCaseClassDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO
