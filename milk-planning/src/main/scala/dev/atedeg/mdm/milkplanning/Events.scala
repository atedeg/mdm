package dev.atedeg.mdm.milkplanning.types

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.Product

/**
 * Events managed by the bounded context.
 */
enum IncomingEvent:
  /**
   * Event representing an order placed used to estimate the [[QuintalsOfMilk quintals of milk]] to be ordered.
   */
  case ReceivedOrder(products: NonEmptyList[RequestedProduct])

/**
 * Events sent by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Event to order the [[QuintalsOfMilk quintals of milk]] needed for the next week.
   * This event is emitted every week on saturday.
   */
  case OrderMilk(n: QuintalsOfMilk)
