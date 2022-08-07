package dev.atedeg.mdm.productionplanning.dto

import java.time.LocalDate

import dev.atedeg.mdm.productionplanning.*
import dev.atedeg.mdm.productionplanning.IncomingEvent.*
import dev.atedeg.mdm.productionplanning.OutgoingEvent.*
import dev.atedeg.mdm.productionplanning.dto.OrderIDDTO.given
import dev.atedeg.mdm.productionplanning.dto.QuantityDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class NewOrderReceivedDTO(order: OrderDTO)
final case class OrderDTO(orderID: String, requiredBy: String, orderedProducts: List[OrderedProductDTO])
final case class OrderedProductDTO(product: ProductDTO, quantity: Int)

object NewOrderReceivedDTO:
  given DTO[NewOrderReceived, NewOrderReceivedDTO] = interCaseClassDTO
  private given DTO[Order, OrderDTO] = interCaseClassDTO
  private given DTO[OrderedProduct, OrderedProductDTO] = interCaseClassDTO

final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
final case class ProductToProduceDTO(product: ProductDTO, quantity: Int)

object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = interCaseClassDTO
  given DTO[ProductionPlan, ProductionPlanDTO] = interCaseClassDTO
  given DTO[ProductToProduce, ProductToProduceDTO] = interCaseClassDTO

final case class OrderDelayedDTO(orderID: String, newDeliveryDate: String)
object OrderDelayedDTO:
  given DTO[OrderDelayed, OrderDelayedDTO] = interCaseClassDTO

private object QuantityDTO:
  given DTO[Quantity, Int] = caseClassDTO

private object OrderIDDTO:
  given DTO[OrderID, String] = caseClassDTO
