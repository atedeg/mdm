package dev.atedeg.mdm.production.dto

import cats.syntax.all.*

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

private object Common:
  given DTO[ProductionID, String] = caseClassDTO
  given DTO[BatchID, String] = caseClassDTO
  given DTO[NumberOfUnits, Int] = caseClassDTO

import Common.given

final case class StartProductionDTO(neededIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
object StartProductionDTO:
  given DTO[StartProduction, StartProductionDTO] = interCaseClassDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO

final case class ProductionEndedDTO(productionID: String, batchID: String)
object ProductionEndedDTO:
  given DTO[ProductionEnded, ProductionEndedDTO] = interCaseClassDTO

final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
final case class ProductToProduceDTO(product: ProductDTO, units: Int)
object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = interCaseClassDTO
  private given DTO[ProductionPlan, ProductionPlanDTO] = interCaseClassDTO
  private given DTO[ProductionPlanItem, ProductToProduceDTO] = interCaseClassDTO

final case class RecipeBookDTO(recipeBook: Map[String, RecipeDTO])
final case class RecipeDTO(recipe: List[QuintalsOfIngredientDTO])
object RecipeBookDTO:
  given DTO[RecipeBook, RecipeBookDTO] = interCaseClassDTO
  private given DTO[Recipe, RecipeDTO] = interCaseClassDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = interCaseClassDTO
  private given DTO[WeightInQuintals, Double] = caseClassDTO

final case class ToStartDTO(id: String, product: ProductDTO, units: Int)
object ToStartDTO:
  given DTO[Production.ToStart, ToStartDTO] = interCaseClassDTO

final case class InProgressDTO(id: String, product: ProductDTO, units: Int)
object InProgressDTO:
  given DTO[Production.InProgress, InProgressDTO] = interCaseClassDTO

final case class EndedDTO(id: String, batchID: String, product: ProductDTO, units: Int)
object EndedDTO:
  given DTO[Production.Ended, EndedDTO] = interCaseClassDTO
