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
  given DTO[OrderID, String] = unwrapFieldDTO
  given DTO[Client, ClientDTO] = productTypeDTO
  given DTO[Location, LocationDTO] = productTypeDTO
  given DTO[Latitude, Double] = unwrapFieldDTO
  given DTO[Longitude, Double] = unwrapFieldDTO
  given DTO[Quantity, Int] = unwrapFieldDTO
  given DTO[ClientName, String] = unwrapFieldDTO
  given DTO[VATNumber, String] = unwrapFieldDTO
  given DTO[PalletizedQuantity, Int] = unwrapFieldDTO
  given DTO[WeightInKilograms, Double] = unwrapFieldDTO

import Commons.*
import Commons.given

given DTO[ClientID, String] = unwrapFieldDTO

final case class OrderReceivedDTO(
    orderLines: List[IncomingOrderLineDTO],
    client: ClientDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)
final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)
object IncomingOrderLineDTO:
  given DTO[IncomingOrderLine, IncomingOrderLineDTO] = productTypeDTO

final case class ClientDTO(code: String, name: String, vatNumber: String)
final case class LocationDTO(latitude: Double, longitude: Double)
object OrderReceivedDTO:
  given DTO[OrderReceived, OrderReceivedDTO] = productTypeDTO

final case class ProductWithQuantityDTO(quantity: Int, product: ProductDTO)

final case class ProductPalletizedForOrderDTO(orderID: String, quantity: Int, product: ProductDTO)
object ProductPalletizedForOrderDTO:
  given DTO[ProductPalletizedForOrder, ProductPalletizedForOrderDTO] = productTypeDTO

final case class OrderCompletedDTO(orderID: String)
object OrderCompletedDTO:
  given DTO[OrderCompleted, OrderCompletedDTO] = productTypeDTO

final case class OrderProcessedDTO(incomingOrder: IncomingOrderDTO)
final case class IncomingOrderDTO(
    id: String,
    orderLines: List[IncomingOrderLineDTO],
    client: ClientDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
)
object OrderProcessedDTO:
  given DTO[OrderProcessed, OrderProcessedDTO] = productTypeDTO
  private given DTO[IncomingOrder, IncomingOrderDTO] = productTypeDTO

final case class PriceInEuroCentsDTO(price: Int)
object PriceInEuroCentsDTO:
  given DTO[PriceInEuroCents, PriceInEuroCentsDTO] = productTypeDTO

final case class InProgressOrderDTO(
    id: String,
    orderLines: List[InProgressOrderLineDTO],
    client: ClientDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
    totalPrice: PriceInEuroCentsDTO,
)
final case class InProgressOrderLineDTO(
    tag: String,
    completeDTO: Option[CompleteOrderLineDTO],
    incompleteDTO: Option[IncompleteOrderLineDTO],
)

final case class CompleteOrderLineDTO(quantity: Int, product: ProductDTO, price: PriceInEuroCentsDTO)
final case class IncompleteOrderLineDTO(actual: Int, required: Int, product: ProductDTO, price: PriceInEuroCentsDTO)

object InProgressOrderDTO:
  given DTO[InProgressOrder, InProgressOrderDTO] = productTypeDTO
  private given DTO[InProgressOrderLine.Complete, CompleteOrderLineDTO] = productTypeDTO
  private given DTO[InProgressOrderLine.Incomplete, IncompleteOrderLineDTO] = productTypeDTO
  private given DTO[InProgressOrderLine, InProgressOrderLineDTO] = sumTypeDTO

final case class ProductPalletizedDTO(product: ProductDTO, quantity: Int)
object ProductPalletizedDTO:
  given DTO[ProductPalletized, ProductPalletizedDTO] = productTypeDTO
  private given DTO[Quantity, Int] = unwrapFieldDTO

final case class CompletedOrderDTO(
    id: String,
    orderLines: List[CompletedOrderLineDTO],
    client: ClientDTO,
    deliveryDate: String,
    deliveryLocation: LocationDTO,
    totalPrice: PriceInEuroCentsDTO,
)
final case class CompletedOrderLineDTO(quantity: Int, product: ProductDTO, price: PriceInEuroCentsDTO)
object CompletedOrderDTO:
  given DTO[CompletedOrder, CompletedOrderDTO] = productTypeDTO
  private given DTO[CompleteOrderLine, CompletedOrderLineDTO] = productTypeDTO

final case class TransportDocumentDTO(
    deliveryLocation: LocationDTO,
    shippingLocation: LocationDTO,
    client: ClientDTO,
    shippingDate: String,
    transportDocumentLines: List[TransportDocumentLineDTO],
    totalWeight: Double,
)
final case class TransportDocumentLineDTO(quantity: Int, product: ProductDTO)
object TransportDocumentDTO:
  given DTO[TransportDocument, TransportDocumentDTO] = productTypeDTO
  private given DTO[TransportDocumentLine, TransportDocumentLineDTO] = productTypeDTO
