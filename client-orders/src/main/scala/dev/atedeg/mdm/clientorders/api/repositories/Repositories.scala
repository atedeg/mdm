package dev.atedeg.mdm.clientorders.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.dto.*

trait PriceListRepository:
  def read[M[_]: Monad: LiftIO]: M[PriceListDTO]

trait OrderRepository:
  def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit]
