package dev.atedeg.mdm.restocking

import cats.data.NonEmptyList

/**
 * The events handled by this bounded context.
 */
enum IncomingEvent:
  /**
   * Received when an order for milk has to be placed.
   */
  case OrderMilk(quintals: QuintalsOfMilk)

  /**
   * Received when a production is started.
   * It consumes the given [[QuintalsOfIngredient ingredients]].
   */
  case ProductionStarted(ingredients: NonEmptyList[QuintalsOfIngredient])
