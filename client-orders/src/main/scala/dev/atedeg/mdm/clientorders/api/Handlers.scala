package dev.atedeg.mdm.clientorders.api

import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.instances.ordering
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

def productPalletizedForOrderHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](
    productPalletizedForOrderDTO: ProductPalletizedForOrderDTO,
): M[Unit] =
  for
    config <- readState
    productPallettizedForOrder <- validate(productPalletizedForOrderDTO)
    order <- config.orderRepository.readInProgressOrder(productPallettizedForOrder.orderID.id.toDTO) >>= validate
    action: Action[PalletizationError, ProductPalletized, InProgressOrder] =
      palletizeProductForOrder(productPallettizedForOrder.quantity, productPallettizedForOrder.product)(order)
    (events, result) = action.execute
    _ <- events.map(_.toDTO[ProductPalletizedDTO]).traverse(config.emitter.emitProductPalletized)
    updatedOrder <- result.leftMap(e => s"Palletization error: $e").getOrRaise
    _ <- config.orderRepository.writeInProgressOrder(updatedOrder.toDTO)
  yield ()

def orderCompletedHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](
    orderCompletedDTO: OrderCompletedDTO,
): M[Unit] =
  for
    config <- readState
    orderCompleted <- validate(orderCompletedDTO)
    order <- config.orderRepository.readInProgressOrder(orderCompleted.orderID.id.toDTO) >>= validate
    action: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(order)
    (_, result) = action.execute
    completedOrder <- result.leftMap(e => s"Order completion error: $e").getOrRaise
    _ <- config.orderRepository.updateOrderToCompleted(completedOrder.toDTO)
  yield ()

def getDDTHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](orderID: String): M[Unit] =
  (readState >>= (_.orderRepository.readCompletedOrder(orderID)) >>= validate)
    .map(o => createTransportDocument(o, weightOrder(o)))
