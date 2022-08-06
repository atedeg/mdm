package dev.atedeg.mdm.utils.serialization

trait Show[A]:
  def toShow(a: A): String
  extension (a: A) def show: String = toShow(a)

trait Read[A]:
  def fromString(s: String): Either[String, A]

extension (s: String) def read[A](using r: Read[A]): Either[String, A] = r.fromString(s)
