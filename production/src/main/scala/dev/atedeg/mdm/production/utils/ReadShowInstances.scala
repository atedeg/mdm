package dev.atedeg.mdm.production.utils

import java.util.UUID
import scala.util.Try

import cats.syntax.all.*

import dev.atedeg.mdm.production.{ BatchID, ProductionID }
import dev.atedeg.mdm.utils.serialization.*

object ReadShowInstances:
  given Read[BatchID] with
    override def fromString(s: String): Either[String, BatchID] =
      Try(UUID.fromString(s))
        .map(BatchID.apply)
        .toEither
        .leftMap(_ => s"Not a valid BatchID: $s")

  given Read[ProductionID] with
    override def fromString(s: String): Either[String, ProductionID] =
      Try(UUID.fromString(s))
        .map(ProductionID.apply)
        .toEither
        .leftMap(_ => s"Not a valid ProductionID: $s")
