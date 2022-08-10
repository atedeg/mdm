package dev.atedeg.mdm.production.api.emitters

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.*

trait Emitter:
  def emitStart[M[_]: Monad: LiftIO](message: StartProductionDTO): M[Unit]
  def emitEnded[M[_]: Monad: LiftIO](message: ProductionEndedDTO): M[Unit]
