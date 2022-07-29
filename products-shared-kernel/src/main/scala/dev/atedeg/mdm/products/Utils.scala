package dev.atedeg.mdm.products

import cats.data.NonEmptyList
import dev.atedeg.mdm.utils.{PositiveNumber, coerce}
import eu.timepit.refined.predicates.all.Positive

import scala.compiletime.*

type OneOf[T <: Tuple] = T match
  case (t *: EmptyTuple) => t
  case (t *: ts) => t | OneOf[ts]

inline def all[T <: Tuple]: NonEmptyList[OneOf[T]] = inline erasedValue[T] match
  case _:(n *: EmptyTuple) =>
    val v = checkInt[n](constValue[n])
    NonEmptyList.one(toGrams(v)).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _:(n *: gs) =>
    val v = checkInt[n](constValue[n])
    NonEmptyList(toGrams(v), all[gs].toList).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _ => compiletime.error("Cannot work on a tuple with elements that are not Grams")

private inline def checkInt[T](inline n: T): Int = inline erasedValue[T] match
  case _: Int => n.asInstanceOf[Int]
  case _ => compiletime.error(codeOf(n) + " is not an int")

private[products] def toGrams(n: Int): Grams = Grams(coerce[Int, Positive](n))