package dev.atedeg.mdm.production.api.emitters

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.*

trait Emitter:
  def emitStartProduction[M[_]: Monad: LiftIO](message: StartProductionDTO): M[Unit]
  def emitNewBatch[M[_]: Monad: LiftIO](message: NewBatchDTO): M[Unit]
