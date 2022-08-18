package dev.atedeg.mdm.restocking.dto

import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class OrderMilkDTO(quintalsOfMilk: Int)
final case class ProductionStartedDTO(quintalsOfIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
type StockDTO = Map[String, Double]

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = productTypeDTO
  private given DTO[QuintalsOfMilk, Int] = unwrapFieldDTO

object ProductionStartedDTO:
  given DTO[ProductionStarted, ProductionStartedDTO] = productTypeDTO

object QuintalsOfIngredientDTO:
  given DTO[QuintalsOfIngredient, QuintalsOfIngredientDTO] = productTypeDTO
  private given DTO[WeightInQuintals, Double] = unwrapFieldDTO

object StockDTO:
  import dev.atedeg.mdm.products.dto.IngredientDTO.given
  given DTO[Stock, StockDTO] = DTO.mapDTO
  private given DTO[StockedQuantity, Double] = unwrapFieldDTO
