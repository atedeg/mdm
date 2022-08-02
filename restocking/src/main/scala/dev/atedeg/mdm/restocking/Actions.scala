package dev.atedeg.mdm.restocking

import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.utils.given

/**
 * Given a list of ingredients needed to [[IncomingEvent.ProductionStarted start a production]],
 * it removes that [[WeightInQuintals quantity]] from the [[Stock stock]].
 */
def consumeIngredients(stock: Stock)(ingredients: NonEmptyList[QuintalsOfIngredient]): Stock =
  ingredients.foldLeft(stock) { case (newStock, QuintalsOfIngredient(q, i)) =>
    val toSubtract = StockedQuantity(q.n)
    newStock.updatedWith(i)(oldQuantity => oldQuantity.map(_ - toSubtract))
  }
