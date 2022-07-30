package dev.atedeg.mdm.products

import scala.compiletime.*

import cats.data.NonEmptyList
import cats.kernel.Order
import dev.atedeg.mdm.utils.{PositiveNumber, coerce}
import eu.timepit.refined.predicates.all.Positive

import scala.compiletime.*

type OneOf[T <: Tuple] = T match
  case (t *: EmptyTuple) => t
  case (t *: ts) => t | OneOf[ts]

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
inline def all[T <: Tuple]: NonEmptyList[OneOf[T]] = inline erasedValue[T] match
  case _: (n *: EmptyTuple) =>
    val v = checkInt[n](constValue[n])
    NonEmptyList.one(v).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _: (n *: gs) =>
    val v = checkInt[n](constValue[n])
    NonEmptyList(v, all[gs].toList).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _ => compiletime.error("Cannot work on a tuple with elements that are not Grams")

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
inline private def checkInt[T](inline n: T): Int = inline erasedValue[T] match
  case _: Int => n.asInstanceOf[Int]
  case _ => compiletime.error(codeOf(n) + " is not an int")

private[products] def toGrams(n: Int): Grams = Grams(coerce[Int, Positive](n))

given Order[Grams] with
  def compare(x: Grams, y: Grams): Int = Order[Int].compare(x.n.value, y.n.value)

extension (cheeseType: CheeseType)

  /**
   * Creates a [[Product product]] with the given [[CheeseType cheese type]] if it can find a
   * weight amongst the allowed ones that matches the given predicate.
   */
  def withWeight(predicate: Int => Boolean): Option[Product] =
    // Somehow Scala does not understand that a function (A => B) could also be treated as a function
    // [a <: A] => a => B and won't compile unless I explicitly convert the function myself...
    def p = [t <: Int] => (n: t) => predicate(n)
    cheeseType match
      case CheeseType.Squacquerone => allSquacqueroneWeights.find(p(_)).map(Product.Squacquerone(_))
      case CheeseType.Casatella => allCasatellaWeights.find(p(_)).map(Product.Casatella(_))
      case CheeseType.Ricotta => allRicottaWeights.find(p(_)).map(Product.Ricotta(_))
      case CheeseType.Stracchino => allStracchinoWeights.find(p(_)).map(Product.Stracchino(_))
      case CheeseType.Caciotta => allCaciottaWeights.find(p(_)).map(Product.Caciotta(_))

/**
 * Returns a [[NonEmptyList list]] of all the allowed weights in [[Grams grams]] for a given [[CheeseType cheese type]].
 */
  def allowedWeights: NonEmptyList[Grams] = cheeseType match
    case CheeseType.Squacquerone => allSquacqueroneWeights.map(toGrams)
    case CheeseType.Casatella => allCasatellaWeights.map(toGrams)
    case CheeseType.Ricotta => allRicottaWeights.map(toGrams)
    case CheeseType.Stracchino => allStracchinoWeights.map(toGrams)
    case CheeseType.Caciotta => allCaciottaWeights.map(toGrams)
