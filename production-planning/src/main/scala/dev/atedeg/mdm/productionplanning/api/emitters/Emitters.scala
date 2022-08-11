package dev.atedeg.mdm.productionplanning.api.emitters

import cats.Monad
import cats.effect.LiftIO
import dev.atedeg.mdm.productionplanning.dto.{OrderDelayedDTO, ProductionPlanReadyDTO}

trait ProductionPlanReadyEmitter:
  def emit[M[_]: Monad: LiftIO](productionPlanReady: ProductionPlanReadyDTO): M[Unit]
  
trait OrderDelayedEmitter:
  def emit[M[_]: Monad: LiftIO](orderDelayed: OrderDelayedDTO): M[Unit]
