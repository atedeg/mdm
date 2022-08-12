package dev.atedeg.mdm.milkplanning.api.emitters

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.milkplanning.dto.OrderMilkDTO

trait OrderMilkEmitter:
  def emit[M[_]: Monad: LiftIO](orderMilkDTO: OrderMilkDTO): M[Unit]
