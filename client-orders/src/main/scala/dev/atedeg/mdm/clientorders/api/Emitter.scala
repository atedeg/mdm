package dev.atedeg.mdm.clientorders.api

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.dto.*

trait Emitter:
  def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit]
  def emitProductPalletized[M[_]: Monad: LiftIO](orderPalletized: ProductPalletizedDTO): M[Unit]

final case class EmitterMQ() extends Emitter:
  override def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit] = ???
  override def emitProductPalletized[M[_]: Monad: LiftIO](productPalletized: ProductPalletizedDTO): M[Unit] = ???
