package dev.atedeg.mdm.products

import scala.compiletime.*

import cats.data.NonEmptyList
import cats.kernel.Order
import eu.timepit.refined.predicates.all.Positive

import dev.atedeg.mdm.utils.{ coerce, PositiveNumber }

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
