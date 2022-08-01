package dev.atedeg.mdm.products.utils

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.*

extension (cheeseType: CheeseType)

  /**
   * Creates a [[Product product]] with the given [[CheeseType cheese type]] if it can find a
   * weight amongst the allowed ones that matches the given predicate.
   */
  def withWeight(predicate: Int => Boolean): Option[Product] =
    // Somehow Scala does not understand that a function (A => B) could also be treated as a function
    // [a <: A] => a => B and won't compile unless I explicitly convert the function myself...
    def p = [T <: Int] => (n: T) => predicate(n)
    cheeseType match
      case CheeseType.Squacquerone => allSquacqueroneWeights.find(p(_)).map(Product.Squacquerone(_))
      case CheeseType.Casatella => allCasatellaWeights.find(p(_)).map(Product.Casatella(_))
      case CheeseType.Ricotta => allRicottaWeights.find(p(_)).map(Product.Ricotta(_))
      case CheeseType.Stracchino => allStracchinoWeights.find(p(_)).map(Product.Stracchino(_))
      case CheeseType.Caciotta => allCaciottaWeights.find(p(_)).map(Product.Caciotta(_))

  /**
   * Returns a [[NonEmptyList list]] of all the allowed weights in [[Grams grams]] for a given [[CheeseType cheese type]].
   */
  def allowedWeights: NonEmptyList[Grams] = (cheeseType match
    case CheeseType.Squacquerone => allSquacqueroneWeights
    case CheeseType.Casatella => allCasatellaWeights
    case CheeseType.Ricotta => allRicottaWeights
    case CheeseType.Stracchino => allStracchinoWeights
    case CheeseType.Caciotta => allCaciottaWeights
  ).map(coerceToGrams)
