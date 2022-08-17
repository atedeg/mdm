package dev.atedeg.mdm.pricing.dto

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
