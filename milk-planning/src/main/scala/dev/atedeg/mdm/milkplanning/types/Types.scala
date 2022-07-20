package dev.atedeg.mdm.milkplanning.types

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import dev.atedeg.mdm.utils.{ NonNegativeNumber, NumberInClosedRange, PositiveDecimal, PositiveNumber }

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

/**
 * Milk processed in order to produce cheese.
 */
final case class ProcessedMilk(quantity: QuintalsOfMilk)

/**
 * A quantity of milk expressed in quintals.
 * @note it must be a [[PositiveDecimal positive decimal number]].
 * @example `QuintalsOfMilk(1.1)` is a valid weight of 110 kg.
 * @example `QuintalsOfMilk(-20.5)` is not a valid weight.
 */
final case class QuintalsOfMilk(n: PositiveDecimal)

/**
 * A [[Week week]] of a given [[Year year]].
 */
final case class Period(week: Week, year: Year)

/**
 * The number of a week in a year
 * @note it must be a [[NumberInClosedRange number]] between 1 and 52 inclusive.
 * @example `Week(1)` is a valid week.
 * @example `Week(54)` is not a valid week.
 */
final case class Week(n: NumberInClosedRange[1, 52])

/**
 * A year.
 * @note it must be a [[PositiveNumber positive number]].
 * @example `Year(2022)` is a valid year.
 * @example `Year(-1000)` is not a valid year.
 */
final case class Year(n: PositiveNumber)

/**
 * It defines the how many [[QuintalsOfMilk quintals of milk]] are needed to produce a quintal of a given
 * [[CheeseType cheese type]].
 */
type RecipeBook = CheeseType => QuintalsOfMilk

/**
 * It defines, for each [[Product product]], the [[StockedQuantity quantity in stock]].
 */
type Stock = Product => StockedQuantity

/**
 * A quantity of a stocked [[Product product]], it may also be zero.
 * @note it must be a [[NonNegativeNumber non-negative number]].
 * @example `StockedQuantity(0)` is valid.
 * @example `StockedQuantity(-1)` is invalid.
 */
final case class StockedQuantity(n: NonNegativeNumber)
