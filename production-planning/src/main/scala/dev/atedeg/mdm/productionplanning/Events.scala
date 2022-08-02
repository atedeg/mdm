package dev.atedeg.mdm.productionplanning

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:
  /**
   * Event representing an order placed used to create the [[ProductionPlan production plan]] of the day.
   */
  case NewOrderReceived(order: Order)

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Events that contains the [[ProductionPlan production plan]] of the day.
   */
  case ProductionPlanReady(productionPlan: ProductionPlan)

  /**
   * If an order cannot be fulfilled since there are some products' ripening days takes more
   * time than the order required date.
   */
  case OrderDelayed()
