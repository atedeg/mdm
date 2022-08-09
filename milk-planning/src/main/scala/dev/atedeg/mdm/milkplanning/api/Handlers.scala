package dev.atedeg.mdm.milkplanning.api

import cats.Monad
import cats.data.NonEmptyList
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.milkplanning.*
import dev.atedeg.mdm.milkplanning.IncomingEvent.ReceivedOrder
import dev.atedeg.mdm.milkplanning.OutgoingEvent.OrderMilk
import dev.atedeg.mdm.milkplanning.api.acl.*
import dev.atedeg.mdm.milkplanning.api.repositories.*
import dev.atedeg.mdm.milkplanning.dto.*
import dev.atedeg.mdm.milkplanning.dto.QuintalsOfMilkDTO.given
import dev.atedeg.mdm.milkplanning.dto.RecipeBookDTO.given
import dev.atedeg.mdm.milkplanning.dto.StockDTO.given
import dev.atedeg.mdm.milkplanning.estimateQuintalsOfMilk
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def receivedOrderHandler[M[_]: Monad: LiftIO: CanRead[ReceivedOrderRepository]: CanRaise[String]](
    incomingOrderDTO: IncomingOrderDTO,
): M[Unit] =
  for
    receivedOrder <- validate(incomingOrderDTO.toReceivedOrderDTO)
    _ <- readState >>= (_.save(receivedOrder.products.toDTO[List[RequestedProductDTO]]))
  yield ()

def orderMilkHandler[M[_]: Monad: LiftIO: CanRead[Configuration]: CanRaise[String]]: M[Unit] =
  for
    config <- readState
    milkPrevYear <- getQuintalsOfThePreviousYear >>= validate
    currentStock <- getCurrentStock >>= validate
    stockedMilk <- getStockedMilk >>= validate
    orderedProducts <- config.receivedOrderRepository.getRequestedProducts
      >>= validate[List[RequestedProductDTO], List[RequestedProduct], M]
    recipeBook <- config.recipeBookRepository.getRecipeBook >>= validate
    action: SafeAction[OrderMilk, QuintalsOfMilk] =
      estimateQuintalsOfMilk(milkPrevYear, orderedProducts, currentStock, recipeBook, stockedMilk)
    (events, _) = action.execute
    _ <- events.map(_.toDTO[OrderMilkDTO]).traverse(config.emitter.emit)
  yield ()

private def getQuintalsOfThePreviousYear[M[_]: Monad: LiftIO]: M[QuintalsOfMilkDTO] = ???
private def getStockedMilk[M[_]: Monad: LiftIO]: M[QuintalsOfMilkDTO] = ???
private def getCurrentStock[M[_]: Monad: LiftIO]: M[StockDTO] = ???
