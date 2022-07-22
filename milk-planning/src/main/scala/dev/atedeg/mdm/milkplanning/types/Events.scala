package dev.atedeg.mdm.milkplanning.types

enum IncomingEvent:
  case ProductRemovedFromStock(product: Product)
  case ProductAddedToStock(product: Product)
  case RestockedMilk(quintalsOfMilk: QuintalsOfMilk)

enum OutgoingEvent:
  case OrderMilk(n: QuintalsOfMilk)
