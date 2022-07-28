package dev.atedeg.mdm.products

import cats.data.NonEmptyList
import dev.atedeg.mdm.utils.{PositiveNumber, coerce}
import eu.timepit.refined.predicates.all.Positive

import scala.compiletime.*

type OneOf[T <: Tuple] = T match
  case (t *: EmptyTuple) => t
  case (t *: ts) => t | OneOf[ts]

inline def all[T <: Tuple]: NonEmptyList[OneOf[T]] = inline erasedValue[T] match
  case _:(Grams[n] *: EmptyTuple) => NonEmptyList.one(Grams(constValue[n])).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _:(Grams[n] *: gs) => NonEmptyList(Grams(constValue[n]), all[gs].toList).asInstanceOf[NonEmptyList[OneOf[T]]]
  case _ => compiletime.error("Cannot work on a tuple with elements that are not Grams")

def toGrams(g: Grams[_ <: Int]): Grams[PositiveNumber] = g.map(coerce[Int, Positive](_))