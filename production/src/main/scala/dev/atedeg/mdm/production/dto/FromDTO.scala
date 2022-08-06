package dev.atedeg.mdm.production.dto
import cats.syntax.all.*
import eu.timepit.refined.numeric.Positive

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.production.utils.ReadShowInstances.given
import dev.atedeg.mdm.products.{ Ingredient, Product }
import dev.atedeg.mdm.products.utils.ReadShowInstancesOps.given
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.serialization.*
import dev.atedeg.mdm.utils.serialization.given

extension (sp: StartProductionDTO)
  def toDomain: Either[String, StartProduction] = sp.neededIngredients.toNel
    .toRight("Needed ingredients is empty")
    .flatMap(_.traverse(_.toDomain))
    .map(StartProduction.apply)

extension (qoi: QuintalsOfIngredientDTO)
  def toDomain: Either[String, QuintalsOfIngredient] = for
    quintals <- qoi.quintals.refined[Positive].map(WeightInQuintals.apply)
    ingredient <- qoi.ingredient.read[Ingredient]
  yield QuintalsOfIngredient(quintals, ingredient)

extension (pe: ProductionEndedDTO)
  def toDomain: Either[String, ProductionEnded] = for
    productionID <- pe.productionID.read[ProductionID]
    batchID <- pe.batchID.read[BatchID]
  yield ProductionEnded(productionID, batchID)

extension (ppr: ProductionPlanReadyDTO)
  def toDomain: Either[String, ProductionPlanReady] = ppr.productionPlan.toNel
    .toRight("The production plan item list is empty")
    .flatMap(_.traverse(_.toDomain))
    .map(ProductionPlan.apply)
    .map(ProductionPlanReady.apply)

extension (ppi: ProductionPlanItemDTO)
  def toDomain: Either[String, ProductionPlanItem] = for
    product <- ppi.product.toDomain
    units <- ppi.units.refined[Positive].map(NumberOfUnits.apply)
  yield ProductionPlanItem(product, units)

extension (p: ProductDTO) def toDomain: Either[String, Product] = ???
