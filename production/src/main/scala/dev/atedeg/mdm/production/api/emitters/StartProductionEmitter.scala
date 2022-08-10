package dev.atedeg.mdm.production.api.emitters

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.StartProductionDTO

trait StartProductionEmitter:
  def emit[M[_]: Monad: LiftIO](message: StartProductionDTO): M[Unit]
