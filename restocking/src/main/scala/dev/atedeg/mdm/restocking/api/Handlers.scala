package dev.atedeg.mdm.restocking.api

import cats.Monad
import cats.data.{ EitherT, Kleisli, ReaderT }
import cats.effect.IO
import cats.syntax.all.*

import dev.atedeg.mdm.products.dto.IngredientDTO.given
import dev.atedeg.mdm.restocking.*
import dev.atedeg.mdm.restocking.IncomingEvent.*
import dev.atedeg.mdm.restocking.Stock
import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.dto.{ OrderMilkDTO, ProductionStartedDTO, StockDTO }
import dev.atedeg.mdm.restocking.dto.StockDTO.given
import dev.atedeg.mdm.utils.serialization.DTOOps.*

type App[C, E, R] = EitherT[[A] =>> ReaderT[IO, C, A], E, R]
final case class DBClient(cs: String)

def remaningQuintalsOfMilkHandler: App[DBClient, String, RemainingMilkDTO] =
  for
    remainingMilkDTO <- readQuintalsFromDB
    remainingMilk <- remainingMilkDTO.toDomain[RemainingMilk].toEitherT
  yield remainingMilk.toDTO[RemainingMilkDTO]

def orderMilkHandler(orderMilkDTO: OrderMilkDTO): EitherT[IO, String, Unit] =
  for
    orderMilk <- orderMilkDTO.toDomain[OrderMilk].toEitherT
    _ <- makeMilkOrder(orderMilk.toDTO[OrderMilkDTO])
  yield ()

def productionStartedHandler(productionStartedDTO: ProductionStartedDTO): EitherT[IO, String, Unit] =
  for
    productionStarted <- productionStartedDTO.toDomain[ProductionStarted].toEitherT
    stock <- readStockFromDB >>= (_.toDomain[Stock].toEitherT)
    newStock = consumeIngredients(stock)(productionStarted.ingredients)
    _ <- writeStockToDB(newStock.toDTO[StockDTO])
  yield ()

private def readQuintalsFromDB: App[DBClient, String, RemainingMilkDTO] = ???
private def makeMilkOrder(orderMilkDTO: OrderMilkDTO): EitherT[IO, String, Unit] = ???
private def readStockFromDB: EitherT[IO, String, StockDTO] = ???
private def writeStockToDB(newStock: StockDTO): EitherT[IO, String, Unit] = ???
