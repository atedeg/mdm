package dev.atedeg.mdm.productionplanning.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.productionplanning.dto.{ CheeseTypeRipeningDaysDTO, OrderDTO, OrderedProductDTO }

trait ReceivedOrderRepository:
  def saveNewOrder[M[_]: Monad: LiftIO](orderedProducts: List[OrderedProductDTO]): M[Unit]
  def getOrders[M[_]: Monad: LiftIO]: M[List[OrderDTO]]

trait RipeningDaysRepository:
  def getRipeningDays[M[_]: Monad: LiftIO]: M[CheeseTypeRipeningDaysDTO]
