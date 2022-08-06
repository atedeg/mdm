package dev.atedeg.mdm.milkplanning.dto

import java.time.format.DateTimeFormatter

import dev.atedeg.mdm.milkplanning.IncomingEvent.*
import dev.atedeg.mdm.milkplanning.OutgoingEvent.*
import dev.atedeg.mdm.milkplanning.RequestedProduct
import dev.atedeg.mdm.products.dto.*
import dev.atedeg.mdm.products.dto.toDTO as toProductDTO

extension (ro: ReceivedOrder) def toDTO: ReceivedOrderDTO = ReceivedOrderDTO(ro.products.map(_.toDTO).toList)

extension (rp: RequestedProduct)
  def toDTO: RequestedProductDTO =
    RequestedProductDTO(
      rp.product.toProductDTO,
      rp.quantity.n.value,
      rp.requiredBy.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    )

extension (om: OrderMilk) def toDTO: OrderMilkDTO = OrderMilkDTO(om.quintalsOfMilk.quintals.value)
