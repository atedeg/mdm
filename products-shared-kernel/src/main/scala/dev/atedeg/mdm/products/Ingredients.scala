package dev.atedeg.mdm.products

/**
 * An ingredient that may be needed by a [[Recipe recipe]] to produce a [[CheeseType type of cheese]].
 */
enum Ingredient:
  case Milk extends Ingredient
  case Cream extends Ingredient
  case Rennet extends Ingredient
  case Salt extends Ingredient
  case Probiotics extends Ingredient