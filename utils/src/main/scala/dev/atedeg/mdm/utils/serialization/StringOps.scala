package dev.atedeg.mdm.utils.serialization

trait Show[A]:
  def toShow(a: A): String
  extension (a: A) def show: String = toShow(a)

trait Read[A]:
  def fromString(s: String): Either[String, A]
  extension (s: String) def read: Either[String, A] = fromString(s)
