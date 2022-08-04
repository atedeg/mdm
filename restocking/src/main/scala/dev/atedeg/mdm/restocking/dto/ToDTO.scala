package dev.atedeg.mdm.restocking.dto

import dev.atedeg.mdm.products.utils.ReadShowInstancesOps.given
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.restocking.QuintalsOfIngredient

extension (om: OrderMilk) def toDTO: OrderMilkDTO = OrderMilkDTO(om.quintals.quintals.value)

extension (ps: ProductionStarted)
  def toDTO: ProductionStartedDTO = ProductionStartedDTO(ps.ingredients.toList.map(_.toDTO))

extension (qoi: QuintalsOfIngredient)
  def toDTO: QuintalsOfIngredientDTO = QuintalsOfIngredientDTO(qoi.quintals.n.value, qoi.ingredient.show)
