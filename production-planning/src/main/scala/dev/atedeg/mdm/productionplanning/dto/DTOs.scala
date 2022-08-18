package dev.atedeg.mdm.productionplanning.dto

import java.time.LocalDate

import dev.atedeg.mdm.productionplanning.*
import dev.atedeg.mdm.productionplanning.IncomingEvent.*
import dev.atedeg.mdm.productionplanning.OutgoingEvent.*
import dev.atedeg.mdm.productionplanning.dto.OrderIDDTO.given
import dev.atedeg.mdm.productionplanning.dto.QuantityDTO.given
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.products.dto.ProductDTO.given
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class OrderDTO(orderID: String, requiredBy: String, orderedProducts: List[OrderedProductDTO])
object OrderDTO:
  given DTO[Order, OrderDTO] = productTypeDTO

private object QuantityDTO:
  given DTO[Quantity, Int] = unwrapFieldDTO

private object OrderIDDTO:
  given DTO[OrderID, String] = unwrapFieldDTO

final case class NewOrderReceivedDTO(order: OrderDTO)
object NewOrderReceivedDTO:
  given DTO[NewOrderReceived, NewOrderReceivedDTO] = productTypeDTO

final case class OrderedProductDTO(product: ProductDTO, quantity: Int)
object OrderedProductDTO:
  given DTO[OrderedProduct, OrderedProductDTO] = productTypeDTO

final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductToProduceDTO(product: ProductDTO, quantity: Int)

final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
object ProductionPlanDTO:
  given DTO[ProductionPlan, ProductionPlanDTO] = productTypeDTO
  given DTO[ProductToProduce, ProductToProduceDTO] = productTypeDTO

object ProductionPlanReadyDTO:
  given DTO[ProductionPlanReady, ProductionPlanReadyDTO] = productTypeDTO

final case class OrderDelayedDTO(orderID: String, newDeliveryDate: String)
object OrderDelayedDTO:
  given DTO[OrderDelayed, OrderDelayedDTO] = productTypeDTO

final case class CheeseTypeRipeningDaysDTO(value: Map[String, Int])
object CheeseTypeRipeningDaysDTO:
  given DTO[CheeseTypeRipeningDays, CheeseTypeRipeningDaysDTO] = productTypeDTO
  private given DTO[RipeningDays, Int] = unwrapFieldDTO

final case class MissingProductsDTO(missingProducts: Map[ProductDTO, Int])
object MissingProductsDTO:
  given DTO[MissingProducts, MissingProductsDTO] = productTypeDTO
  private given DTO[MissingQuantity, Int] = unwrapFieldDTO
