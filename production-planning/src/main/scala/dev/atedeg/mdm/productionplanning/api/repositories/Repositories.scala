package dev.atedeg.mdm.productionplanning.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.productionplanning.dto.{ CheeseTypeRipeningDaysDTO, OrderDTO }

trait ReceivedOrderRepository:
  def saveNewOrder[M[_]: Monad: LiftIO](order: OrderDTO): M[Unit]
  def getOrders[M[_]: Monad: LiftIO]: M[List[OrderDTO]]

trait RipeningDaysRepository:
  def getRipeningDays[M[_]: Monad: LiftIO]: M[CheeseTypeRipeningDaysDTO]
