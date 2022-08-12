package dev.atedeg.mdm.production

import java.time.{ LocalDate, LocalDateTime }

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.CheeseType

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
  case NewBatch(batchID: BatchID, cheeseType: CheeseType, readyFrom: LocalDate)

enum IncomingEvent:
  /**
   * Specifies the [[ProductionPlan production plan]] for the day with
   * the [[Product products]] that need to be produced.
   */
  case ProductionPlanReady(productionPlan: ProductionPlan)

  /**
   * Fired when a [[Production.InProgress production]] is terminated by a smart machine.
   */
  case ProductionEnded(productionID: ProductionID)
