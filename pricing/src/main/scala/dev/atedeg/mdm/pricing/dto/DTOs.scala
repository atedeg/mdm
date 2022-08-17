package dev.atedeg.mdm.pricing.dto

import cats.syntax.all.*

import dev.atedeg.mdm.pricing.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class IncomingOrderLineDTO(quantity: Int, product: ProductDTO)

object IncomingOrderLineDTO:
  given DTO[IncomingOrderLine, IncomingOrderLineDTO] = interCaseClassDTO
  private given DTO[Quantity, Int] = caseClassDTO

final case class PriceInEuroCentsDTO(price: Int)

object PriceInEuroCentsDTO:
  given DTO[PriceInEuroCents, PriceInEuroCentsDTO] = interCaseClassDTO

final case class PriceListDTO(priceList: Map[ProductDTO, PriceInEuroCentsDTO])

object PriceListDTO:
  given DTO[PriceList, PriceListDTO] = interCaseClassDTO

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
  given DTO[Promotion, PromotionDTO] = interCaseClassDTO
  private given DTO[PromotionLine, PromotionLineDTO] with
    override def elemToDto(e: PromotionLine): PromotionLineDTO = e match
      case f: PromotionLine.Fixed => PromotionLineDTO("fixed", Some(f.toDTO), None)
      case t: PromotionLine.Threshold => PromotionLineDTO("threshold", None, Some(t.toDTO))

    override def dtoToElem(dto: PromotionLineDTO): Either[String, PromotionLine] = dto.tag match
      case "fixed" =>
        dto.fixedDTO match
          case Some(dto) => dto.toDomain[PromotionLine.Fixed]
          case None => "Found tag 'fixed' but fixed data is missing".asLeft[PromotionLine]
      case "threshold" =>
        dto.thresholdDTO match
          case Some(dto) => dto.toDomain[PromotionLine.Threshold]
          case None => "Found tag 'threshold' but threshold data is missing".asLeft[PromotionLine]
      case s => s"Unknown tag: $s".asLeft[PromotionLine]
  private given DTO[PromotionLine.Fixed, FixedPromotionLineDTO] = interCaseClassDTO
  private given DTO[PromotionLine.Threshold, ThresholdPromotionLineDTO] = interCaseClassDTO
  private given DTO[ThresholdQuantity, Int] = caseClassDTO
  private given DTO[DiscountPercentage, Double] = caseClassDTO
  private given DTO[Client, ClientDTO] = interCaseClassDTO

given DTO[ClientID, String] = caseClassDTO
