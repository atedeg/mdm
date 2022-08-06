package dev.atedeg.mdm.products.dto

import cats.syntax.all.*
import eu.timepit.refined.numeric.Positive

import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.utils.*
import dev.atedeg.mdm.products.utils.ReadShowInstances.given
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.serialization.*

extension (p: ProductDTO)
  def toDomain: Either[String, Product] =
    for
      weight <- p.weight.refined[Positive].map(Grams.apply)
      cheeseType <- p.cheeseType.read[CheeseType]
      product <- cheeseType
        .withWeight(_ === weight.n.value)
        .toRight(s"Couldn't find a `$cheeseType` with weight $weight")
    yield product
