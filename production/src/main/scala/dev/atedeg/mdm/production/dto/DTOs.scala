package dev.atedeg.mdm.production.dto

import java.time.LocalDateTime

import cats.syntax.all.*

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.products.CheeseType
import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

given DTO[ProductionID, String] = unwrapFieldDTO

private object Common:
  given DTO[BatchID, String] = unwrapFieldDTO
  given DTO[NumberOfUnits, Int] = unwrapFieldDTO

import Common.given

final case class StartProductionDTO(neededIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
object StartProductionDTO:
  given DTO[StartProduction, StartProductionDTO] = productTypeDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = productTypeDTO
  private given DTO[WeightInQuintals, Double] = unwrapFieldDTO

final case class NewBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object NewBatchDTO:
  given DTO[NewBatch, NewBatchDTO] = productTypeDTO

final case class ProductionEndedDTO(productionID: String)
object ProductionEndedDTO:
  given DTO[ProductionEnded, ProductionEndedDTO] = productTypeDTO

final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
final case class ProductToProduceDTO(product: ProductDTO, units: Int)
object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = productTypeDTO
  private given DTO[ProductionPlan, ProductionPlanDTO] = productTypeDTO
  private given DTO[ProductionPlanItem, ProductToProduceDTO] = productTypeDTO

final case class RecipeBookDTO(recipeBook: Map[String, RecipeDTO])
final case class RecipeDTO(recipe: List[QuintalsOfIngredientDTO])
object RecipeBookDTO:
  given DTO[RecipeBook, RecipeBookDTO] = productTypeDTO
  private given DTO[Recipe, RecipeDTO] = productTypeDTO
  private given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = productTypeDTO
  private given DTO[WeightInQuintals, Double] = unwrapFieldDTO

final case class ToStartDTO(id: String, product: ProductDTO, units: Int)
object ToStartDTO:
  given DTO[Production.ToStart, ToStartDTO] = productTypeDTO

final case class InProgressDTO(id: String, product: ProductDTO, units: Int)
object InProgressDTO:
  given DTO[Production.InProgress, InProgressDTO] = productTypeDTO

final case class EndedDTO(id: String, batchID: String, product: ProductDTO, units: Int)
object EndedDTO:
  given DTO[Production.Ended, EndedDTO] = productTypeDTO

final case class CheeseTypeRipeningDaysDTO(value: Map[String, Int])
object CheeseTypeRipeningDaysDTO:
  given DTO[CheeseTypeRipeningDays, CheeseTypeRipeningDaysDTO] = productTypeDTO
  private given DTO[RipeningDays, Int] = unwrapFieldDTO
