package dev.atedeg.mdm.milkplanning.dto

import dev.atedeg.mdm.milkplanning.{
  Quantity,
  QuintalsOfMilk,
  RecipeBook,
  RequestedProduct,
  Stock,
  StockedQuantity,
  Yield,
}
import dev.atedeg.mdm.milkplanning.IncomingEvent.ReceivedOrder
import dev.atedeg.mdm.milkplanning.OutgoingEvent.OrderMilk
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class ReceivedOrderDTO(products: List[RequestedProductDTO])
final case class RequestedProductDTO(product: ProductDTO, quantity: Int, requiredBy: String)
final case class OrderMilkDTO(quintals: Int)
final case class QuintalsOfMilkDTO(quintals: Int)
type StockDTO = Map[ProductDTO, Int]
type RecipeBookDTO = Map[String, Double]

object ReceivedOrderDTO:
  given DTO[ReceivedOrder, ReceivedOrderDTO] = interCaseClassDTO
  private given DTO[RequestedProduct, RequestedProductDTO] = interCaseClassDTO
  private given DTO[Quantity, Int] = caseClassDTO
  
object RequestedProductDTO:
  given DTO[RequestedProduct, RequestedProductDTO] = interCaseClassDTO
  private given DTO[Quantity, Int] = caseClassDTO

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = interCaseClassDTO
  private given DTO[QuintalsOfMilk, Int] = caseClassDTO

object StockDTO:
  import dev.atedeg.mdm.products.dto.ProductDTO.given
  given DTO[Stock, StockDTO] = DTO.mapDTO
  private given DTO[StockedQuantity, Int] = caseClassDTO

object RecipeBookDTO:
  import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
  import dev.atedeg.mdm.products.dto.ProductDTO.given
  given DTO[RecipeBook, RecipeBookDTO] = DTO.mapDTO
  private given DTO[Yield, Double] = caseClassDTO

object QuintalsOfMilkDTO:
  given DTO[QuintalsOfMilk, QuintalsOfMilkDTO] = interCaseClassDTO
