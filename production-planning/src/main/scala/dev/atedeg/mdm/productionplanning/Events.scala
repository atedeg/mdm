package dev.atedeg.mdm.productionplanning

enum IncomingEvent:
  case NewOrderReceived(order: Order)

enum OutgoingEvent:
  case ProductionPlanReady(productionPlan: ProductionPlan)
