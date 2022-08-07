package dev.atedeg.mdm.stocking.dto

import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.stocking.*
import dev.atedeg.mdm.stocking.IncomingEvent.*
import dev.atedeg.mdm.stocking.LabelledProduct
import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*

final case class ProductStockedDTO(labelledProduct: LabelledProductDTO)
final case class LabelledProductDTO(product: ProductDTO, quantity: Int, batchID: String)
object ProductStockedDTO:
  given DTO[ProductStocked, ProductStockedDTO] = interCaseClassDTO
  private given DTO[LabelledProduct, LabelledProductDTO] = interCaseClassDTO
  private given DTO[AvailableQuantity, Int] = caseClassDTO

private given DTO[BatchID, String] = caseClassDTO

final case class BatchReadyForQualityAssuranceDTO(batch: String)
object BatchReadyForQualityAssuranceDTO:
  given DTO[BatchReadyForQualityAssurance, BatchReadyForQualityAssuranceDTO] = interCaseClassDTO

final case class ProductRemovedFromStockDTO(quantity: Int, product: ProductDTO)
object ProductRemovedFromStockDTO:
  given DTO[ProductRemovedFromStock, ProductRemovedFromStockDTO] = interCaseClassDTO
  given DTO[DesiredQuantity, Int] = caseClassDTO

final case class NewBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object NewBatchDTO:
  given DTO[NewBatch, NewBatchDTO] = interCaseClassDTO
