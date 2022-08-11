package dev.atedeg.mdm.productionplanning.api.acl

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import cats.data.NonEmptyList

import dev.atedeg.mdm.productionplanning.dto.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

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
    val date = LocalDateTime.parse(io.deliveryDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate
    OrderDTO(io.id, date.toDTO[String], io.orderLines.map(_.toOrderedProductDTO))

extension (io: IncomingOrderDTO) def toNewOrderReceivedDTO: NewOrderReceivedDTO = NewOrderReceivedDTO(io.toNewOrderDTO)

final case class DesiredStockDTO(desiredStock: List[(ProductDTO, Int)])
extension (ds: DesiredStockDTO) def toMissingProductsInStock: StockDTO = StockDTO(ds.desiredStock.toMap)
