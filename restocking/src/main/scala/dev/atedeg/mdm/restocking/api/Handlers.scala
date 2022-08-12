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
    : M[RemainingMilkDTO] = (readState >>= (_.getQuintals) >>= validate).map(_.toDTO[RemainingMilkDTO])

def orderMilkHandler[M[_]: Monad: LiftIO: CanRaise[String]](orderMilkDTO: OrderMilkDTO): M[Unit] =
  validate(orderMilkDTO) >>= (o => makeMilkOrder(o.toDTO[OrderMilkDTO]))

def productionStartedHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[StockRepository]](
    productionStartedDTO: ProductionStartedDTO,
): M[Unit] =
  for
    stockRepository <- readState
    productionStarted <- validate(productionStartedDTO)
    stock <- stockRepository.getStock >>= validate
    newStock = consumeIngredients(stock)(productionStarted.ingredients)
    _ <- stockRepository.writeStock(newStock.toDTO[StockDTO])
  yield ()

private def makeMilkOrder[M[_]: Monad: LiftIO](orderMilkDTO: OrderMilkDTO): M[Unit] = ().pure
