package dev.atedeg.mdm.production

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Fired when an [[Ingredient ingredient]] is used to [[startProduction() start a production]].
   * It specifies the [[Ingredient ingredient]] used and the [[WeightInQuintals quintals]] consumed.
   */
  case IngredientUsed(ingredient: Ingredient, weight: WeightInQuintals)

  /**
   * Fired when a [[Production.InProgress production]] is terminated, given a
   * [[LotNumber lot number]] and sent to the refrigeration room.
   */
  case ProductionEnded(productionID: ProductionID, lotNumber: LotNumber)
