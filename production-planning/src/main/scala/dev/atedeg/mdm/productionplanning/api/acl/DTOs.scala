package dev.atedeg.mdm.productionplanning.api.acl

import java.time.LocalDate

import cats.data.NonEmptyList

import dev.atedeg.mdm.productionplanning.dto.*
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
  def toOrderedProductDTO: OrderedProductDTO = OrderedProductDTO(iol.product, iol.quantity)

extension (io: IncomingOrderDTO)
  def toNewOrderDTO: OrderDTO =
    OrderDTO(io.id, io.deliveryDate, io.orderLines.map(_.toOrderedProductDTO))

extension (io: IncomingOrderDTO) def toNewOrderReceivedDTO: NewOrderReceivedDTO = NewOrderReceivedDTO(io.toNewOrderDTO)
