package dev.atedeg.mdm.products

import dev.atedeg.mdm.utils.*

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * A weight in grams.
 */
final case class Grams(n: PositiveNumber)

/**
 * A type of cheese.
 */
enum CheeseType:
  case Squacquerone
  case Casatella
  case Ricotta
  case Stracchino
  case Caciotta

/**
 * A [[CheeseType type of cheese]] with its respective [[Grams weight]].
 */
enum Product(val cheeseType: CheeseType, val weight: Grams):
  case Squacquerone(w: SquacqueroneWeightInGrams) extends Product(CheeseType.Squacquerone, toGrams(w))
  case Casatella(w: CasatellaWeightInGrams) extends Product(CheeseType.Casatella, toGrams(w))
  case Ricotta(w: RicottaWeightInGrams) extends Product(CheeseType.Ricotta, toGrams(w))
  case Stracchino(w: StracchinoWeightInGrams) extends Product(CheeseType.Stracchino, toGrams(w))
  case Caciotta(w: CaciottaWeightInGrams) extends Product(CheeseType.Caciotta, toGrams(w))

type SquacqueroneWeightsInGrams = (100, 250, 350, 800, 1000, 1500)
type SquacqueroneWeightInGrams = OneOf[SquacqueroneWeightsInGrams]
val allSquacqueroneWeights = all[SquacqueroneWeightsInGrams]

type CasatellaWeightsInGrams = (300, 350, 800, 1000)
type CasatellaWeightInGrams = OneOf[CasatellaWeightsInGrams]
val allCasatellaWeights = all[CasatellaWeightsInGrams]

type RicottaWeightsInGrams = (350, 1800)
type RicottaWeightInGrams = OneOf[RicottaWeightsInGrams]
val allRicottaWeights = all[RicottaWeightsInGrams]

type StracchinoWeightsInGrams = (250, 1000)
type StracchinoWeightInGrams = OneOf[StracchinoWeightsInGrams]
val allStracchinoWeights = all[StracchinoWeightsInGrams]

type CaciottaWeightsInGrams = (500, 1000)
type CaciottaWeightInGrams = OneOf[CaciottaWeightsInGrams]
val allCaciottaWeights = all[CaciottaWeightsInGrams]

object Product:
  def unapply(prod: Product): (CheeseType, Grams) = (prod.cheeseType, prod.weight)
