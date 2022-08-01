package dev.atedeg.mdm.production

import dev.atedeg.mdm.products.CheeseType

/**
 * Error raised in case there is no [[Recipe recipe]] for a given [[CheeseType cheese type]].
 */
final case class MissingRecipe(forType: CheeseType)
