package dev.atedeg.mdm.production.dto

type ProductDTO = String

final case class StartProductionDTO(neededIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
final case class ProductionEndedDTO(productionID: String, batchID: String)
final case class ProductionPlanReadyDTO(productionPlan: List[ProductionPlanItemDTO])
final case class ProductionPlanItemDTO(product: ProductDTO, units: Int)

