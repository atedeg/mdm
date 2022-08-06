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
  case StartProduction(neededIngredients: NonEmptyList[QuintalsOfIngredient])

  /**
   * Fired when a [[Production.InProgress production]] is terminated, given a
   * [[BatchID batch ID]] and sent to the refrigeration room.
   */
  case ProductionEnded(productionID: ProductionID, batchID: BatchID)

enum IncomingEvent:
  /**
   * Specifies the [[ProductionPlan production plan]] for the day with
   * the [[Product products]] that need to be produced.
   */
  case ProductionPlanReady(productionPlan: ProductionPlan)
