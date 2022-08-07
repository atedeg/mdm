package dev.atedeg.mdm.clientorders.dto

import dev.atedeg.mdm.clientorders.*
import dev.atedeg.mdm.clientorders.IncomingEvent.*
import dev.atedeg.mdm.clientorders.OutgoingEvent.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*

private object Commons:
  final case class CustomerDTO(code: String, name: String, vatNumber: String)
  final case class LocationDTO(latitude: Double, longitude: Double)
  final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)

  given DTO[OrderID, String] = caseClassDTO
  given DTO[Customer, CustomerDTO] = interCaseClassDTO
  given DTO[Location, LocationDTO] = interCaseClassDTO
  given DTO[Latitude, Double] = caseClassDTO
  given DTO[Longitude, Double] = caseClassDTO
  given DTO[Quantity, Int] = caseClassDTO
  given DTO[CustomerID, String] = caseClassDTO
  given DTO[CustomerName, String] = caseClassDTO
  given DTO[VATNumber, String] = caseClassDTO
  given DTO[IncomingOrderLine, IncomingOrderLineDTO] = interCaseClassDTO

import Commons.*
import Commons.given

final case class OrderReceivedDTO(
    id: String,
    orderLines: List[IncomingOrderLineDTO],
    customer: CustomerDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)
object OrderReceivedDTO:
  given DTO[OrderReceived, OrderReceivedDTO] = interCaseClassDTO

final case class ProductPalletizedForOrderDTO(orderID: String, quantity: Int, product: ProductDTO)
object ProductPalletizedForOrderDTO:
  given DTO[ProductPalletizedForOrder, ProductPalletizedForOrderDTO] = interCaseClassDTO

final case class OrderCompletedDTO(orderID: String)
object OrderCompletedDTO:
  given DTO[OrderCompleted, OrderCompletedDTO] = interCaseClassDTO

final case class OrderProcessedDTO(incomingOrder: IncomingOrderDTO)
final case class IncomingOrderDTO(
    id: String,
    orderLines: List[IncomingOrderLineDTO],
    customer: CustomerDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)
object OrderProcessedDTO:
  given DTO[OrderProcessed, OrderProcessedDTO] = interCaseClassDTO
  private given DTO[IncomingOrder, IncomingOrderDTO] = interCaseClassDTO
