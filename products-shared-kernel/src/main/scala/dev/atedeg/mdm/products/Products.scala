package dev.atedeg.mdm.products

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.predicates.all.Positive
import eu.timepit.refined.refineV

/**
 * A weight in grams.
 */
final case class Grams[N <: Int | PositiveNumber](n : N) {
  def map[M <: Int | PositiveNumber](f: N => M): Grams[M] = Grams(f(n))
}

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
enum Product(val cheeseType: CheeseType, val weight: Grams[_ <: PositiveNumber]):
  case Squacquerone(w: SquacqueroneWeight) extends Product(CheeseType.Squacquerone, toGrams(w))
  case Casatella(w: CasatellaWeight) extends Product(CheeseType.Casatella, toGrams(w))
  case Ricotta(w: RicottaWeight) extends Product(CheeseType.Ricotta, toGrams(w))
  case Stracchino(w: StracchinoWeight) extends Product(CheeseType.Stracchino, toGrams(w))
  case Caciotta(w: CaciottaWeight) extends Product(CheeseType.Caciotta, toGrams(w))

type SquacqueroneWeights = (Grams[100], Grams[250], Grams[350], Grams[800], Grams[1000], Grams[1500])
type SquacqueroneWeight = OneOf[SquacqueroneWeights]
val allSquacqueroneWeights = all[SquacqueroneWeights]

type CasatellaWeights = (Grams[300], Grams[350], Grams[800], Grams[1000])
type CasatellaWeight = OneOf[CasatellaWeights]
val allCasatellaWeights = all[CasatellaWeights]

type RicottaWeights = (Grams[350], Grams[1800])
type RicottaWeight = OneOf[RicottaWeights]
val allRicottaWeights = all[RicottaWeights]

type StracchinoWeights = (Grams[250], Grams[1000])
type StracchinoWeight = OneOf[StracchinoWeights]
val allStracchinoWeights = all[StracchinoWeights]

type CaciottaWeights = (Grams[500], Grams[1000])
type CaciottaWeight = OneOf[CaciottaWeights]
val allCaciottaWeights = all[CaciottaWeights]
