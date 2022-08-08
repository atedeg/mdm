package dev.atedeg.mdm.restocking.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.restocking.Stock
import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.api.repositories.StockRepository
import dev.atedeg.mdm.restocking.dto.{ OrderMilkDTO, ProductionStartedDTO, StockDTO }
import dev.atedeg.mdm.restocking.dto.StockDTO.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def remaningQuintalsOfMilkHandler[M[_]: Monad: LiftIO: CanRead[StockRepository]: CanRaise[String]]
    : M[RemainingMilkDTO] =
  for
    remainingMilkDTO <- readState >>= (_.getQuintals)
    remainingMilk <- remainingMilkDTO.toDomain[RemainingMilk].getOrRaise
  yield remainingMilk.toDTO[RemainingMilkDTO]

def orderMilkHandler[M[_]: Monad: LiftIO: CanRaise[String]](orderMilkDTO: OrderMilkDTO): M[Unit] =
  for
    orderMilk <- orderMilkDTO.toDomain[OrderMilk].getOrRaise
    _ <- makeMilkOrder(orderMilk.toDTO[OrderMilkDTO])
  yield ()

def productionStartedHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[StockRepository]](
    productionStartedDTO: ProductionStartedDTO,
): M[Unit] =
  for
    stockRepository <- readState
    productionStarted <- productionStartedDTO.toDomain[ProductionStarted].getOrRaise
    stock <- stockRepository.getStock >>= (_.toDomain[Stock].getOrRaise)
    newStock = consumeIngredients(stock)(productionStarted.ingredients)
    _ <- stockRepository.writeStock(newStock.toDTO[StockDTO])
  yield ()

private def makeMilkOrder[M[_]: Monad: LiftIO](orderMilkDTO: OrderMilkDTO): M[Unit] = ().pure
