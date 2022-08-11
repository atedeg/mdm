package dev.atedeg.mdm.clientorders.dto

import cats.syntax.all.*

import dev.atedeg.mdm.clientorders.*
import dev.atedeg.mdm.clientorders.IncomingEvent.*
import dev.atedeg.mdm.clientorders.OutgoingEvent.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

private object Commons:
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
  given DTO[PriceInEuroCents, Int] = caseClassDTO
  given DTO[PalletizedQuantity, Int] = caseClassDTO

import Commons.*
import Commons.given

final case class OrderReceivedDTO(
    orderLines: List[IncomingOrderLineDTO],
    customer: CustomerDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)
final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)
final case class CustomerDTO(code: String, name: String, vatNumber: String)
final case class LocationDTO(latitude: Double, longitude: Double)
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

final case class PriceListDTO(priceList: Map[ProductDTO, Int])
object PriceListDTO:
  given DTO[PriceList, PriceListDTO] = interCaseClassDTO

final case class InProgressOrderDTO(
    id: String,
    orderLines: List[InProgressOrderLineDTO],
    customer: CustomerDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
    totalPrice: Int,
)
final case class InProgressOrderLineDTO(
    tag: String,
    completeDTO: Option[CompleteOrderLineDTO],
    incompleteDTO: Option[IncompleteOrderLineDTO],
)

final case class CompleteOrderLineDTO(quantity: Int, product: ProductDTO, price: Int)
final case class IncompleteOrderLineDTO(actual: Int, required: Int, product: ProductDTO, price: Int)

object InProgressOrderDTO:
  given DTO[InProgressOrder, InProgressOrderDTO] = interCaseClassDTO
  private given DTO[InProgressOrderLine.Complete, CompleteOrderLineDTO] = interCaseClassDTO
  private given DTO[InProgressOrderLine.Incomplete, IncompleteOrderLineDTO] = interCaseClassDTO
  private given DTO[InProgressOrderLine, InProgressOrderLineDTO] = new DTO:
    override def elemToDto(e: InProgressOrderLine): InProgressOrderLineDTO = e match
      case c: InProgressOrderLine.Complete => InProgressOrderLineDTO("complete", Some(c.toDTO), None)
      case i: InProgressOrderLine.Incomplete => InProgressOrderLineDTO("incomplete", None, Some(i.toDTO))
    override def dtoToElem(dto: InProgressOrderLineDTO): Either[String, InProgressOrderLine] = dto.tag match
      case "complete" =>
        dto.completeDTO match
          case Some(dto) => dto.toDomain[InProgressOrderLine.Complete]
          case None => "Found tag 'complete' but complete data is missing".asLeft[InProgressOrderLine]
      case "incomplete" =>
        dto.incompleteDTO match
          case Some(dto) => dto.toDomain[InProgressOrderLine.Incomplete]
          case None => "Found tag 'incomplete' but incomplete data is missing".asLeft[InProgressOrderLine]
      case s => s"Unknown tag: $s".asLeft[InProgressOrderLine]

final case class ProductPalletizedDTO(product: ProductDTO, quantity: Int)
object ProductPalletizedDTO:
  given DTO[ProductPalletized, ProductPalletizedDTO] = interCaseClassDTO
  private given DTO[Quantity, Int] = caseClassDTO
