package dev.atedeg.mdm.production

import cats.data.NonEmptyList

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Fired when a [[Production.ToStart production]] needs to be started, specifies the
   * [[QuintalsOfIngredient needed ingredients and the quantity]] necessary to sustain the
   * production.
   */
  case StartProduction(neededIngredient: NonEmptyList[QuintalsOfIngredient])

  /**
   * Fired when a [[Production.InProgress production]] is terminated, given a
   * [[LotNumber lot number]] and sent to the refrigeration room.
   */
  case ProductionEnded(productionID: ProductionID, batchID: BatchID)
