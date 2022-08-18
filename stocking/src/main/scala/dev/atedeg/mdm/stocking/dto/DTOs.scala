package dev.atedeg.mdm.stocking.dto

import dev.atedeg.mdm.products.*
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
  given DTO[ProductStocked, ProductStockedDTO] = productTypeDTO
  private given DTO[LabelledProduct, LabelledProductDTO] = productTypeDTO
  private given DTO[AvailableQuantity, Int] = unwrapFieldDTO

private given DTO[BatchID, String] = unwrapFieldDTO

final case class BatchReadyForQualityAssuranceEventDTO(batch: String)
object BatchReadyForQualityAssuranceEventDTO:
  given DTO[BatchReadyForQualityAssurance, BatchReadyForQualityAssuranceEventDTO] = productTypeDTO

final case class ProductRemovedFromStockDTO(quantity: Int, product: ProductDTO)
object ProductRemovedFromStockDTO:
  given DTO[ProductRemovedFromStock, ProductRemovedFromStockDTO] = productTypeDTO
  given DTO[Quantity, Int] = unwrapFieldDTO

final case class NewBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object NewBatchDTO:
  given DTO[NewBatch, NewBatchDTO] = productTypeDTO

final case class AvailableStockDTO(availableStock: List[ProductAvailableQuantityDTO])
final case class ProductAvailableQuantityDTO(product: ProductDTO, availableQuantity: Int)
object AvailableStockDTO:
  given DTO[AvailableStock, AvailableStockDTO] = productTypeDTO
  private given DTO[(Product, AvailableQuantity), ProductAvailableQuantityDTO] = productTypeDTO
  private given DTO[AvailableQuantity, Int] = unwrapFieldDTO

final case class DesiredStockDTO(desiredStock: List[ProductDesiredQuantityDTO])
final case class ProductDesiredQuantityDTO(product: ProductDTO, desiredQuantity: Int)
object DesiredStockDTO:
  given DTO[DesiredStock, DesiredStockDTO] = productTypeDTO
  private given DTO[(Product, DesiredQuantity), ProductDesiredQuantityDTO] = productTypeDTO
  private given DTO[DesiredQuantity, Int] = unwrapFieldDTO

final case class AgingBatchDTO(batchID: String, cheeseType: String, readyFrom: String)
object AgingBatchDTO:
  given DTO[Batch.Aging, AgingBatchDTO] = productTypeDTO

final case class QualityAssuredBatchPassedDTO(id: String, cheeseType: String)
object QualityAssuredBatchPassedDTO:
  given DTO[QualityAssuredBatch.Passed, QualityAssuredBatchPassedDTO] = productTypeDTO

final case class QualityAssuredBatchFailedDTO(id: String, cheeseType: String)
object QualityAssuredBatchFailedDTO:
  given DTO[QualityAssuredBatch.Failed, QualityAssuredBatchFailedDTO] = productTypeDTO

final case class BatchReadyForQualityAssuranceDTO(id: String, cheeseType: String)
object BatchReadyForQualityAssuranceDTO:
  given DTO[Batch.ReadyForQualityAssurance, BatchReadyForQualityAssuranceDTO] = productTypeDTO
