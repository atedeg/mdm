package dev.atedeg.mdm.products

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
 * A [[CheeseType type of cheese]] with its respective [[Size size]].
 */
enum Product(val cheeseType: CheeseType):
  case Squacquerone(size: SquacqueroneSizeInGrams) extends Product(CheeseType.Squacquerone)
  case Casatella(size: CasatellaSizeInGrams) extends Product(CheeseType.Casatella)
  case Ricotta(size: RicottaSizeInGrams) extends Product(CheeseType.Ricotta)
  case Stracchino(size: StracchinoSizeInGrams) extends Product(CheeseType.Stracchino)
  case Caciotta(size: CaciottaSizeInGrams) extends Product(CheeseType.Caciotta)

type SquacqueroneSizeInGrams = 100 | 250 | 350 | 800 | 1000 | 1500
type CasatellaSizeInGrams = 300 | 350 | 800 | 1000
type RicottaSizeInGrams = 350 | 1800
type StracchinoSizeInGrams = 250 | 1000
type CaciottaSizeInGrams = 1200
