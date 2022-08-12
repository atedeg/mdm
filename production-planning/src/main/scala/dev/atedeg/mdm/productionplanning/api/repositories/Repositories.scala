package dev.atedeg.mdm.productionplanning.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.productionplanning.dto.{ CheeseTypeRipeningDaysDTO, OrderDTO, ProductionPlanDTO }

trait ReceivedOrderRepository:
  def saveNewOrder[M[_]: Monad: LiftIO](order: OrderDTO): M[Unit]
  def getOrders[M[_]: Monad: LiftIO]: M[List[OrderDTO]]

trait RipeningDaysRepository:
  def getRipeningDays[M[_]: Monad: LiftIO]: M[CheeseTypeRipeningDaysDTO]

trait ProductionPlanRepository:
  def getPreviuosYearProductionPlan[M[_]: Monad: LiftIO]: M[Option[ProductionPlanDTO]]
  def saveProductionPlan[M[_]: Monad: LiftIO](productionPlanDTO: ProductionPlanDTO): M[Unit]
