package dev.atedeg.mdm.restocking.dto

final case class OrderMilkDTO(quintalsOfMilk: Int)
final case class ProductionStartedDTO(quintalsOfIngredients: List[QuintalsOfIngredientDTO])
final case class QuintalsOfIngredientDTO(quintals: Double, ingredient: String)
