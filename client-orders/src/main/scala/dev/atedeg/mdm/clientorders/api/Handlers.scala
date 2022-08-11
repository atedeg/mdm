package dev.atedeg.mdm.clientorders.api

import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.clientorders.*
import dev.atedeg.mdm.clientorders.IncomingEvent.*
import dev.atedeg.mdm.clientorders.OutgoingEvent.*
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def newOrderHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](
    orderReceivedDTO: OrderReceivedDTO,
): M[String] =
  for
    config <- readState
    priceList <- config.priceListRepository.read >>= validate
    orderData <- validate(orderReceivedDTO)
    incomingOrder = IncomingOrder(
      OrderID(UUID.randomUUID),
      orderData.orderLines,
      orderData.customer,
      orderData.deliveryDate,
      orderData.deliveryLocation,
    )
    action: SafeAction[OrderProcessed, PricedOrder] = processIncomingOrder(priceList)(incomingOrder)
    (events, pricedOrder) = action.execute
    _ <- events.map(_.toDTO[OrderProcessedDTO]).traverse(config.emitter.emitOrderProcessed)
    inProgressOrder = startPreparingOrder(pricedOrder)
    _ <- config.orderRepository.writeInProgressOrder(inProgressOrder.toDTO)
  yield pricedOrder.id.id.toDTO[String]

/*
def productPalletizedForOrderHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](
    productPalletizedForOrderDTO: ProductPalletizedForOrderDTO,
): M[Unit] =
  for
    config <- readState

  yield ()
 */
