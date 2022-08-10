package dev.atedeg.mdm.stocking.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.stocking.QualityAssuredBatch
import dev.atedeg.mdm.stocking.dto.*
import dev.atedeg.mdm.utils.monads.CanRaise

trait StockRepository:
  def readStock[M[_]: Monad: LiftIO]: M[AvailableStockDTO]
  def writeStock[M[_]: Monad: LiftIO](updatedStock: AvailableStockDTO): M[Unit]
  def readDesiredStock[M[_]: Monad: LiftIO]: M[DesiredStockDTO]

trait BatchesRepository:
  def addNewBatch[M[_]: Monad: LiftIO](agingBatch: AgingBatchDTO): M[Unit]
  def readReadyForQA[M[_]: Monad: LiftIO: CanRaise[String]](id: String): M[BatchReadyForQualityAssuranceDTO]
  def approveBatch[M[_]: Monad: LiftIO](passedBatch: QualityAssuredBatchPassedDTO): M[Unit]
  def rejectBatch[M[_]: Monad: LiftIO](failedBatch: QualityAssuredBatchFailedDTO): M[Unit]

final case class StockRepositoryDB(connectionString: String) extends StockRepository:
  def readStock[M[_]: Monad: LiftIO]: M[AvailableStockDTO] = ???
  def writeStock[M[_]: Monad: LiftIO](updatedStock: AvailableStockDTO): M[Unit] = ???
  def readDesiredStock[M[_]: Monad: LiftIO]: M[DesiredStockDTO] = ???

final case class BatchesRepositoryDB(connectionString: String) extends BatchesRepository:
  def addNewBatch[M[_]: Monad: LiftIO](agingBatch: AgingBatchDTO): M[Unit] = ???
  def readReadyForQA[M[_]: Monad: LiftIO: CanRaise[String]](id: String): M[BatchReadyForQualityAssuranceDTO] = ???
  def approveBatch[M[_]: Monad: LiftIO](passedBatch: QualityAssuredBatchPassedDTO): M[Unit] = ???
  def rejectBatch[M[_]: Monad: LiftIO](failedBatch: QualityAssuredBatchFailedDTO): M[Unit] = ???
