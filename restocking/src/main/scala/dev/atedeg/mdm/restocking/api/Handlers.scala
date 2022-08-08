package dev.atedeg.mdm.restocking.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.restocking.Stock
import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.dto.{ OrderMilkDTO, ProductionStartedDTO, StockDTO }
import dev.atedeg.mdm.restocking.dto.StockDTO.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class DBClient()
def remaningQuintalsOfMilkHandler[M[_]: Monad: LiftIO: CanRead[DBClient]: CanRaise[String]]: M[RemainingMilkDTO] =
  for
    remainingMilkDTO <- readQuintalsFromDB
    remainingMilk <- remainingMilkDTO.toDomain[RemainingMilk].getOrRaise
  yield remainingMilk.toDTO[RemainingMilkDTO]

def orderMilkHandler[M[_]: Monad: LiftIO: CanRaise[String]](orderMilkDTO: OrderMilkDTO): M[Unit] =
  for
    orderMilk <- orderMilkDTO.toDomain[OrderMilk].getOrRaise
    _ <- makeMilkOrder(orderMilk.toDTO[OrderMilkDTO])
  yield ()

def productionStartedHandler[M[_]: Monad: LiftIO: CanRaise[String]](
    productionStartedDTO: ProductionStartedDTO,
): M[Unit] =
  for
    productionStarted <- productionStartedDTO.toDomain[ProductionStarted].getOrRaise
    stock <- readStockFromDB >>= (_.toDomain[Stock].getOrRaise)
    newStock = consumeIngredients(stock)(productionStarted.ingredients)
    _ <- writeStockToDB(newStock.toDTO[StockDTO])
  yield ()

private def readQuintalsFromDB[M[_]: Monad: LiftIO]: M[RemainingMilkDTO] = RemainingMilkDTO(10).pure
private def makeMilkOrder[M[_]: Monad: LiftIO](orderMilkDTO: OrderMilkDTO): M[Unit] = ().pure
private def readStockFromDB[M[_]: Monad: LiftIO]: M[StockDTO] = Map("Milk" -> 10.2).pure
private def writeStockToDB[M[_]: Monad: LiftIO](newStock: StockDTO): M[Unit] = ().pure
