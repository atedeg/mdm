package dev.atedeg.mdm.restocking.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import org.scalatest.EitherValues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.api.repositories.StockRepository
import dev.atedeg.mdm.restocking.dto.{ ProductionStartedDTO, QuintalsOfIngredientDTO, StockDTO }
import dev.atedeg.mdm.utils.monads.*

trait Mocks:
  @SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
  var inMemoryStockDTO: Option[StockDTO] = None
  val stockRepository: StockRepository = new StockRepository:
    override def getQuintals[M[_]: Monad: LiftIO]: M[RemainingMilkDTO] = RemainingMilkDTO(10).pure
    override def getStock[M[_]: Monad: LiftIO]: M[StockDTO] = Map("milk" -> 20.0, "salt" -> 3.0, "rennet" -> 30.5).pure
    override def writeStock[M[_]: Monad: LiftIO](newStock: StockDTO): M[Unit] =
      inMemoryStockDTO = Some(newStock)
      ().pure

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `remaningQuintalsOfMilkHandler`" should {
    "return the same value it reads from the DB" in {
      val handler: ServerAction[StockRepository, String, RemainingMilkDTO] = remaningQuintalsOfMilkHandler
      val result = handler.unsafeExecute(stockRepository)
      result.value shouldBe RemainingMilkDTO(10)
    }
  }

  "The `productionStartedHandler`" should {
    "write the new stock to DB" in {
      val consumedIngredients = List(QuintalsOfIngredientDTO(10.0, "milk"))
      val productionStartedDTO = ProductionStartedDTO(consumedIngredients)
      val handler: ServerAction[StockRepository, String, Unit] = productionStartedHandler(productionStartedDTO)
      handler.unsafeExecute(stockRepository)
      inMemoryStockDTO should contain(Map("milk" -> 10.0, "salt" -> 3.0, "rennet" -> 30.5))
    }
  }
