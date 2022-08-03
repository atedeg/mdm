package dev.atedeg.mdm.productionplanning

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:
  /**
   * Event representing an [[Order order]] placed, data about the orders is used to create the [[ProductionPlan production plan]].
   */
  case NewOrderReceived(order: Order)

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Event that contains the [[ProductionPlan production plan]] of the day.
   */
  case ProductionPlanReady(productionPlan: ProductionPlan)

  /**
   * An event emitted if an [[Order order]] cannot be fulfilled since there are some [[Product products]] whose
   * [[RipeningDays ripening days]] would make it impossible to fulfil the order by the required date.
   */
  case OrderDelayed(orderID: OrderID)
