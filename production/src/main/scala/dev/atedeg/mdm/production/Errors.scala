package dev.atedeg.mdm.production

/**
 * Error raised in case there is no [[Recipe recipe]] for a given [[CheeseType cheese type]].
 */
final case class MissingRecipe(forType: CheeseType)
