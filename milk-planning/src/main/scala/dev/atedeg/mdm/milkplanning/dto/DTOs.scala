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
  given DTO[ReceivedOrder, ReceivedOrderDTO] = productTypeDTO
  private given DTO[RequestedProduct, RequestedProductDTO] = productTypeDTO
  private given DTO[Quantity, Int] = unwrapFieldDTO

object RequestedProductDTO:
  given DTO[RequestedProduct, RequestedProductDTO] = productTypeDTO
  private given DTO[Quantity, Int] = unwrapFieldDTO

object OrderMilkDTO:
  given DTO[OrderMilk, OrderMilkDTO] = productTypeDTO
  private given DTO[QuintalsOfMilk, Int] = unwrapFieldDTO

object StockDTO:
  import dev.atedeg.mdm.products.dto.ProductDTO.given
  given DTO[Stock, StockDTO] = DTO.mapDTO
  private given DTO[StockedQuantity, Int] = unwrapFieldDTO

object RecipeBookDTO:
  import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
  import dev.atedeg.mdm.products.dto.ProductDTO.given
  given DTO[RecipeBook, RecipeBookDTO] = DTO.mapDTO
  private given DTO[Yield, Double] = unwrapFieldDTO

object QuintalsOfMilkDTO:
  given DTO[QuintalsOfMilk, QuintalsOfMilkDTO] = productTypeDTO
