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

final case class BatchReadyForQualityAssuranceEventDTO(batch: String)
object BatchReadyForQualityAssuranceEventDTO:
  given DTO[BatchReadyForQualityAssurance, BatchReadyForQualityAssuranceEventDTO] = interCaseClassDTO

final case class ProductRemovedFromStockDTO(quantity: Int, product: ProductDTO)
object ProductRemovedFromStockDTO:
  given DTO[ProductRemovedFromStock, ProductRemovedFromStockDTO] = interCaseClassDTO
  given DTO[Quantity, Int] = caseClassDTO

final case class NewBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object NewBatchDTO:
  given DTO[NewBatch, NewBatchDTO] = interCaseClassDTO

final case class AvailableStockDTO(as: List[(ProductDTO, Int)])
object AvailableStockDTO:
  given DTO[AvailableStock, AvailableStockDTO] = interCaseClassDTO
  private given DTO[AvailableQuantity, Int] = caseClassDTO

final case class DesiredStockDTO(ds: List[(ProductDTO, Int)])
object DesiredStockDTO:
  given DTO[DesiredStock, DesiredStockDTO] = interCaseClassDTO
  private given DTO[DesiredQuantity, Int] = caseClassDTO

final case class AgingBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object AgingBatchDTO:
  given DTO[Batch.Aging, AgingBatchDTO] = interCaseClassDTO

final case class QualityAssuredBatchPassedDTO(id: String, cheeseType: String)
object QualityAssuredBatchPassedDTO:
  given DTO[QualityAssuredBatch.Passed, QualityAssuredBatchPassedDTO] = interCaseClassDTO

final case class QualityAssuredBatchFailedDTO(id: String, cheeseType: String)
object QualityAssuredBatchFailedDTO:
  given DTO[QualityAssuredBatch.Failed, QualityAssuredBatchFailedDTO] = interCaseClassDTO  

final case class BatchReadyForQualityAssuranceDTO(id: String, cheeseType: String)
object BatchReadyForQualityAssuranceDTO:
  given DTO[Batch.ReadyForQualityAssurance, BatchReadyForQualityAssuranceDTO] = interCaseClassDTO
