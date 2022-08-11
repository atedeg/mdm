package dev.atedeg.mdm.clientorders.api

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.dto.OrderProcessedDTO

trait Emitter:
  def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit]

final case class EmitterMQ() extends Emitter:
  override def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit] = ???
