package dev.atedeg.mdm.clientorders.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.InProgressOrder
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.utils.monads.*

trait OrderRepository:
  def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit]
  def readInProgressOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[InProgressOrderDTO]
  def updateOrderToCompleted[M[_]: Monad: LiftIO: CanRaise[String]](completedOrder: CompletedOrderDTO): M[Unit]
  def readCompletedOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[CompletedOrderDTO]

final class OrderRepositoryDB(connectionString: String) extends OrderRepository:
  override def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit] = ???
  override def readInProgressOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[InProgressOrderDTO] = ???
  override def updateOrderToCompleted[M[_]: Monad: LiftIO: CanRaise[String]](order: CompletedOrderDTO): M[Unit] = ???
  override def readCompletedOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[CompletedOrderDTO] = ???
