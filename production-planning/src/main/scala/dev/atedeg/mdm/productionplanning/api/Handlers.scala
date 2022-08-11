package dev.atedeg.mdm.productionplanning.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.productionplanning.{ createProductionPlan, Order, OrderedProduct, ProductionPlan }
import dev.atedeg.mdm.productionplanning.OutgoingEvent
import dev.atedeg.mdm.productionplanning.OutgoingEvent.{ OrderDelayed, ProductionPlanReady }
import dev.atedeg.mdm.productionplanning.api.Configuration.*
import dev.atedeg.mdm.productionplanning.api.acl.IncomingOrderDTO
import dev.atedeg.mdm.productionplanning.api.acl.toNewOrderReceivedDTO
import dev.atedeg.mdm.productionplanning.api.repositories.ReceivedOrderRepository
import dev.atedeg.mdm.productionplanning.dto.*
import dev.atedeg.mdm.productionplanning.dto.NewOrderReceivedDTO.given
import dev.atedeg.mdm.productionplanning.dto.OrderDTO.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def handleOrderReceived[M[_]: Monad: LiftIO: CanRead[ReceivedOrderRepository]: CanRaise[String]](
    incomingOrderDTO: IncomingOrderDTO,
): M[Unit] =
  for
    newOrder <- validate(incomingOrderDTO.toNewOrderReceivedDTO)
    _ <- readState >>= (_.saveNewOrder(newOrder.order.toDTO[OrderDTO]))
  yield ()

def handleSendProductionPlan[M[_]: Monad: LiftIO: CanRead[Configuration]: CanRaise[String]]: M[Unit] =
  for
    config <- readState
    stock <- getMissingProductsFromStock >>= validate
    cheeseTypeRipeningDays <- config.ripeningDaysRepository.getRipeningDays >>= validate
    previousProductionPlan <- getPreviousYearProductionPlan >>= validate
    orders <- config.receivedOrderRepository.getOrders >>= validate[List[OrderDTO], List[Order], M]
    action: SafeActionTwoEvents[ProductionPlanReady, OrderDelayed, ProductionPlan] =
      createProductionPlan(stock, cheeseTypeRipeningDays)(previousProductionPlan, orders)
    (events1, events2, _) = action.execute
    _ <- events2.map(_.toDTO[OrderDelayedDTO]).traverse(config.orderDelayedEmitter.emit)
    _ <- events1.map(_.toDTO[ProductionPlanReadyDTO]).traverse(config.productionPlanReadyEmitter.emit)
  yield ()

private def getMissingProductsFromStock[M[_]: Monad: LiftIO]: M[StockDTO] = ???
private def getPreviousYearProductionPlan[M[_]: Monad: LiftIO]: M[ProductionPlanDTO] = ???
