package dev.atedeg.mdm.stocking.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.stocking.*
import dev.atedeg.mdm.stocking.Error.NotEnoughStock
import dev.atedeg.mdm.stocking.IncomingEvent.ProductRemovedFromStock
import dev.atedeg.mdm.stocking.api.acl.ProductPalletizedDTO
import dev.atedeg.mdm.stocking.api.acl.toProductRemovedFromStockDTO
import dev.atedeg.mdm.stocking.api.repositories.{ BatchesRepository, StockRepository }
import dev.atedeg.mdm.stocking.dto.*
import dev.atedeg.mdm.stocking.dto.AvailableStockDTO.given
import dev.atedeg.mdm.stocking.dto.DesiredStockDTO.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def handleRemovalFromStock[M[_]: Monad: LiftIO: CanRead[StockRepository]: CanRaise[String]](
    productPalletized: ProductPalletizedDTO,
): M[Unit] =
  for
    repository <- readState
    removed <- validate(productPalletized.toProductRemovedFromStockDTO)
    stock <- repository.readStock >>= validate
    action: Action[NotEnoughStock, Unit, AvailableStock] = removeFromStock(stock)(removed.product, removed.quantity)
    (_, res) = action.execute
    updatedStock <- res.leftMap(e => s"Not enough in stock: $e").getOrRaise
    _ <- repository.writeStock(updatedStock.toDTO[AvailableStockDTO])
  yield ()

def handleNewBatch[M[_]: Monad: LiftIO: CanRead[BatchesRepository]: CanRaise[String]](
    newBatchDTO: NewBatchDTO,
): M[Unit] =
  for
    newBatch <- validate(newBatchDTO)
    batch: Batch.Aging = Batch.Aging(newBatch.batchID, newBatch.cheeseType, newBatch.readyFrom)
    _ <- readState >>= (_.addNewBatch(batch.toDTO[AgingBatchDTO]))
  yield ()

def handleDesiredStockRequest[M[_]: Monad: LiftIO: CanRead[StockRepository]: CanRaise[String]]: M[DesiredStockDTO] =
  (readState >>= (_.readDesiredStock) >>= validate).map(_.toDTO)

def handleProductsInStockRequest[M[_]: Monad: LiftIO: CanRead[StockRepository]: CanRaise[String]]
    : M[AvailableStockDTO] = (readState >>= (_.readStock) >>= validate).map(_.toDTO)

def approveBatchHandler[M[_]: Monad: LiftIO: CanRead[BatchesRepository]: CanRaise[String]](batchID: String): M[Unit] =
  for
    repository <- readState
    batchReady <- repository.readReadyForQA(batchID) >>= validate
    approvedBatch = approveBatch(batchReady)
    _ <- repository.approveBatch(approvedBatch.toDTO)
  yield ()

def rejectBatchHandler[M[_]: Monad: LiftIO: CanRead[BatchesRepository]: CanRaise[String]](batchID: String): M[Unit] =
  for
    repository <- readState
    batchReady <- repository.readReadyForQA(batchID) >>= validate
    rejectedBatch = rejectBatch(batchReady)
    _ <- repository.rejectBatch(rejectedBatch.toDTO)
  yield ()
