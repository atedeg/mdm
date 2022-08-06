package dev.atedeg.mdm.restocking.dto

import cats.syntax.all.*
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.predicates.all.NonNegative
import eu.timepit.refined.refineV

import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.utils.ReadShowInstances.given
import dev.atedeg.mdm.restocking.{ QuintalsOfIngredient, QuintalsOfMilk, WeightInQuintals }
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.serialization.*

extension (omDTO: OrderMilkDTO)
  def toDomain: Either[String, OrderMilk] =
    omDTO.quintalsOfMilk.refined[Positive].map(QuintalsOfMilk.apply).map(OrderMilk.apply)

extension (psDTO: ProductionStartedDTO)
  def toDomain: Either[String, ProductionStarted] =
    psDTO.quintalsOfIngredients.toNel
      .toRight("The quintals of ingredients list is empty")
      .flatMap(_.traverse(_.toDomain))
      .map(ProductionStarted.apply)

extension (qoiDTO: QuintalsOfIngredientDTO)
  def toDomain: Either[String, QuintalsOfIngredient] =
    for
      weight <- qoiDTO.quintals.refined[Positive].map(WeightInQuintals.apply)
      ingredient <- qoiDTO.ingredient.read[Ingredient]
    yield QuintalsOfIngredient(weight, ingredient)
