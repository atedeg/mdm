package dev.atedeg.mdm.stocking.api

import java.time.{ LocalDate, LocalDateTime }
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.collection.mutable

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import org.scalatest.EitherValues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.stocking.AvailableStock
import dev.atedeg.mdm.stocking.api.acl.ProductPalletizedDTO
import dev.atedeg.mdm.stocking.api.repositories.{ BatchesRepository, StockRepository }
import dev.atedeg.mdm.stocking.dto.*
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.monads.ServerAction
import dev.atedeg.mdm.utils.serialization.DTOOps.*

trait Mocks:
  val product: ProductDTO = ProductDTO("caciotta", 500)
  @SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
  var availableStock: AvailableStockDTO = AvailableStockDTO(List((product, 5)))
  val desiredStock: DesiredStockDTO = DesiredStockDTO(List((product, 2)))
  val stockRepository: StockRepository = new StockRepository:
    override def readStock[M[_]: Monad: LiftIO]: M[AvailableStockDTO] = availableStock.pure
    override def writeStock[M[_]: Monad: LiftIO](updatedStock: AvailableStockDTO): M[Unit] =
      availableStock = updatedStock
      ().pure
    override def readDesiredStock[M[_]: Monad: LiftIO]: M[DesiredStockDTO] = desiredStock.pure

  @SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
  var agingBatches: List[AgingBatchDTO] = Nil
  val batchesRepository: BatchesRepository = new BatchesRepository:
    override def addNewBatch[M[_]: Monad: LiftIO](agingBatch: AgingBatchDTO): M[Unit] =
      agingBatches = agingBatch :: agingBatches
      ().pure

    override def readReadyForQA[M[_]: Monad: LiftIO: CanRaise[String]](
        id: String,
    ): M[BatchReadyForQualityAssuranceDTO] = ???
    override def approveBatch[M[_]: Monad: LiftIO](passedBatch: QualityAssuredBatchPassedDTO): M[Unit] = ???
    override def rejectBatch[M[_]: Monad: LiftIO](failedBatch: QualityAssuredBatchFailedDTO): M[Unit] = ???

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `handleRemovalFromStock`" should {
    "correctly update the available stock when a product has been palletized" in {
      val productPalletized = ProductPalletizedDTO(product, 1)
      val handler: ServerAction[StockRepository, String, Unit] = handleRemovalFromStock(productPalletized)
      handler.unsafeExecute(stockRepository)
      availableStock shouldBe AvailableStockDTO(List((product, 4)))
    }
  }
  "The `handleDesiredStockRequest`" should {
    "return the same value it reads from the DB" in {
      val handler: ServerAction[StockRepository, String, DesiredStockDTO] = handleDesiredStockRequest
      val res = handler.unsafeExecute(stockRepository)
      res.value shouldBe desiredStock
    }
  }

  "The `handleNewBatch`" should {
    "add a batch to the aging ones" in {
      val id = UUID.randomUUID.toDTO[String]
      val date = LocalDateTime.now.toDTO[String]
      val newBatch = NewBatchDTO(id, "caciotta", date)
      val handler: ServerAction[BatchesRepository, String, Unit] = handleNewBatch(newBatch)
      handler.unsafeExecute(batchesRepository)
      val agingBatchRes = AgingBatchDTO(newBatch.batchID, newBatch.cheeseType, newBatch.readyFrom)
      agingBatches should contain(agingBatchRes)
    }
  }

  "The `handleProductsInStockRequest`" should {
    "return the same value it reads from the DB" in {
      val handler: ServerAction[StockRepository, String, AvailableStockDTO] = handleProductsInStockRequest
      val res = handler.unsafeExecute(stockRepository)
      res.value shouldBe availableStock
    }
  }
