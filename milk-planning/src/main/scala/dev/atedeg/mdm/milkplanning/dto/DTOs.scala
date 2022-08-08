package dev.atedeg.mdm.milkplanning.dto

import dev.atedeg.mdm.milkplanning.{ Quantity, QuintalsOfMilk, RequestedProduct }
import dev.atedeg.mdm.milkplanning.IncomingEvent.ReceivedOrder
import dev.atedeg.mdm.milkplanning.OutgoingEvent.OrderMilk
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class ReceivedOrderDTO(products: List[RequestedProductDTO])
final case class RequestedProductDTO(product: ProductDTO, quantity: Int, requiredBy: String)
final case class OrderMilkDTO(quintals: Int)

object ReceivedOrderDTO:
  given DTO[ReceivedOrder, ReceivedOrderDTO] = interCaseClassDTO
  private given DTO[RequestedProduct, RequestedProductDTO] = interCaseClassDTO
  private given DTO[Quantity, Int] = caseClassDTO

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = interCaseClassDTO
  private given DTO[QuintalsOfMilk, Int] = caseClassDTO
