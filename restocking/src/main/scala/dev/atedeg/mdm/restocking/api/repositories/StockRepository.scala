package dev.atedeg.mdm.restocking.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.dto.{ OrderMilkDTO, StockDTO }

trait StockRepository:
  def getQuintals[M[_]: Monad: LiftIO]: M[RemainingMilkDTO]
  def getStock[M[_]: Monad: LiftIO]: M[StockDTO]
  def writeStock[M[_]: Monad: LiftIO](newStock: StockDTO): M[Unit]

final case class DBStockRepository(connectionString: String) extends StockRepository:
  override def getQuintals[M[_]: Monad: LiftIO]: M[RemainingMilkDTO] = ???
  override def getStock[M[_]: Monad: LiftIO]: M[StockDTO] = ???
  override def writeStock[M[_]: Monad: LiftIO](newStock: StockDTO): M[Unit] = ???
