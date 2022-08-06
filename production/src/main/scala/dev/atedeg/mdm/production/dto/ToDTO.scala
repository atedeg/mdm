package dev.atedeg.mdm.production.dto

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.*
import dev.atedeg.mdm.products.utils.ReadShowInstancesOps.given

extension (sp: StartProduction)
  def toDTO: StartProductionDTO = StartProductionDTO(sp.neededIngredients.map(_.toDTO).toList)

extension (qoi: QuintalsOfIngredient)
  def toDTO: QuintalsOfIngredientDTO = QuintalsOfIngredientDTO(qoi.quintals.n.value, qoi.ingredient.show)

extension (pe: ProductionEnded)
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def toDTO: ProductionEndedDTO = ProductionEndedDTO(pe.productionID.ID.toString, pe.batchID.ID.toString)

extension (ppr: ProductionPlanReady)
  def toDTO: ProductionPlanReadyDTO = ProductionPlanReadyDTO(ppr.productionPlan.plan.map(_.toDTO).toList)

extension (ppi: ProductionPlanItem)
  def toDTO: ProductionPlanItemDTO = ProductionPlanItemDTO(??? /*ppi.productToProduce.toDTO*/, ppi.units.n.value)
