package dev.atedeg.mdm.restocking

import cats.data.NonEmptyList

enum IncomingEvent:
  case OrderMilk(quintals: QuintalsOfMilk)
  case ProductionStarted(ingredients: NonEmptyList[QuintalsOfIngredient])

