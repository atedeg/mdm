package dev.atedeg.mdm.milkplanning.api.acl

import dev.atedeg.mdm.milkplanning.dto.*
import dev.atedeg.mdm.products.dto.ProductDTO

final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)
final case class CustomerDTO(code: String, name: String, vatNumber: String)
final case class LocationDTO(latitude: Double, longitude: Double)
final case class IncomingOrderDTO(
    id: String,
    orderLines: List[IncomingOrderLineDTO],
    customer: CustomerDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)

extension (iol: IncomingOrderLineDTO)
  def toRequestedProductDTO(requiredBy: String): RequestedProductDTO =
    RequestedProductDTO(iol.product, iol.quantity, requiredBy)

extension (io: IncomingOrderDTO)
  def toReceivedOrderDTO: ReceivedOrderDTO = ReceivedOrderDTO(
    io.orderLines.map(_.toRequestedProductDTO(io.deliveryDate)),
  )

final case class OrderedMilkDTO(quintals: Int, orderPlacedAt: String)
extension (om: OrderedMilkDTO) def toQuintalsOfMilkDTO: QuintalsOfMilkDTO = QuintalsOfMilkDTO(om.quintals)
