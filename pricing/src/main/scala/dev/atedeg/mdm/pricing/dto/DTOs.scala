package dev.atedeg.mdm.pricing.dto

import cats.syntax.all.*

import dev.atedeg.mdm.pricing.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)

object IncomingOrderLineDTO:
  given DTO[IncomingOrderLine, IncomingOrderLineDTO] = productTypeDTO
  private given DTO[Quantity, Int] = unwrapFieldDTO

final case class PriceInEuroCentsDTO(price: Int)

object PriceInEuroCentsDTO:
  given DTO[PriceInEuroCents, PriceInEuroCentsDTO] = productTypeDTO

final case class PriceListDTO(priceList: Map[ProductDTO, PriceInEuroCentsDTO])

object PriceListDTO:
  given DTO[PriceList, PriceListDTO] = productTypeDTO

final case class PromotionDTO(client: ClientDTO, expiryDate: String, lines: List[PromotionLineDTO])
final case class PromotionLineDTO(
    tag: String,
    fixedDTO: Option[FixedPromotionLineDTO],
    thresholdDTO: Option[ThresholdPromotionLineDTO],
)
final case class FixedPromotionLineDTO(product: ProductDTO, discountPercentage: Double)
final case class ThresholdPromotionLineDTO(product: ProductDTO, threshold: Int, discountPercentage: Double)

final case class ClientDTO(code: String)

object PromotionDTO:
  given DTO[Promotion, PromotionDTO] = productTypeDTO
  private given DTO[PromotionLine, PromotionLineDTO] = sumTypeDTO
  private given DTO[PromotionLine.Fixed, FixedPromotionLineDTO] = productTypeDTO
  private given DTO[PromotionLine.Threshold, ThresholdPromotionLineDTO] = productTypeDTO
  private given DTO[ThresholdQuantity, Int] = unwrapFieldDTO
  private given DTO[DiscountPercentage, Double] = unwrapFieldDTO
  private given DTO[Client, ClientDTO] = productTypeDTO

given DTO[ClientID, String] = unwrapFieldDTO
