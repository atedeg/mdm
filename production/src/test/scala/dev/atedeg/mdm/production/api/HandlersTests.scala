package dev.atedeg.mdm.production.api

import java.time.LocalDate
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.production.api.emitters.Emitter
import dev.atedeg.mdm.production.api.repositories.{
  CheeseTypeRipeningDaysRepository,
  ProductionsRepository,
  RecipeBookRepository,
}
import dev.atedeg.mdm.production.dto.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.monads.ServerAction
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

@SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
trait Mocks:
  var emittedStarts: List[StartProductionDTO] = Nil
  var emittedNews: List[NewBatchDTO] = Nil
  var savedInProgressProductions: List[InProgressDTO] = Nil
  var ended: Option[EndedDTO] = None

  val productionsRepository: ProductionsRepository = new ProductionsRepository:
    override def writeInProgressProductions[M[_]: Monad: LiftIO](productions: List[InProgressDTO]): M[Unit] =
      savedInProgressProductions = productions
      ().pure
    override def readInProgressProduction[M[_]: Monad: LiftIO: CanRaise[String]](
        productionID: String,
    ): M[InProgressDTO] = InProgressDTO(productionID, ProductDTO("ricotta", 350), 30).pure
    override def updateToEnded[M[_]: Monad: LiftIO](production: EndedDTO): M[Unit] =
      ended = Some(production)
      ().pure

  val recipeBookRepository: RecipeBookRepository = new RecipeBookRepository:
    override def read[M[_]: Monad: LiftIO]: M[RecipeBookDTO] =
      RecipeBookDTO(Map("ricotta" -> RecipeDTO(List(QuintalsOfIngredientDTO(10, "milk"))))).pure

  val ripeningDaysRepository: CheeseTypeRipeningDaysRepository = new CheeseTypeRipeningDaysRepository:
    override def read[M[_]: Monad: LiftIO]: M[CheeseTypeRipeningDaysDTO] =
      CheeseTypeRipeningDaysDTO(Map("ricotta" -> 0)).pure

  val emitter: Emitter = new Emitter:
    override def emitStartProduction[M[_]: Monad: LiftIO](message: StartProductionDTO): M[Unit] =
      emittedStarts = message :: emittedStarts
      ().pure
    override def emitNewBatch[M[_]: Monad: LiftIO](message: NewBatchDTO): M[Unit] =
      emittedNews = message :: emittedNews
      ().pure

  val configuration: Configuration =
    Configuration(recipeBookRepository, productionsRepository, ripeningDaysRepository, emitter)

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `handleProductionPlanReady` handler" should {
    val productsToProduce = List(ProductToProduceDTO(ProductDTO("ricotta", 350), 1000))
    val productionPlan = ProductionPlanDTO(productsToProduce)
    val productionPlanReady = ProductionPlanReadyDTO(productionPlan)
    val action: ServerAction[Configuration, String, Unit] = handleProductionPlanReady(productionPlanReady)
    action.unsafeExecute(configuration)

    "write the new productions to the DB" in {
      savedInProgressProductions match
        case Nil => fail("No productions were saved")
        case List(saved) =>
          saved.product shouldBe ProductDTO("ricotta", 350)
          saved.units shouldBe 1000
        case _ => fail("Saved more productions than expected")
    }
    "emit all the domain events" in {
      emittedStarts shouldBe List(StartProductionDTO(List(QuintalsOfIngredientDTO(35, "milk"))))
    }
  }

  "The `handleProductionPlanEnded` handler" should {
    val productionID = UUID.randomUUID.toDTO[String]
    val productionEndedDTO = ProductionEndedDTO(productionID)
    val action: ServerAction[Configuration, String, Unit] = handleProductionEnded(productionEndedDTO)
    action.unsafeExecute(configuration)
    "update the production in the DB" in {
      ended match
        case None => fail("The production was not updated")
        case Some(updated) =>
          updated.id shouldBe productionID
          updated.product shouldBe ProductDTO("ricotta", 350)
          updated.units shouldBe 30
    }
    "emit all the domain events" in {
      emittedNews match
        case Nil => fail("No event was emitted")
        case List(emitted) =>
          emitted.cheeseType shouldBe "ricotta"
          emitted.readyFrom shouldBe LocalDate.now.toDTO[String]
        case _ => fail("Emitted more events than expected")
    }
  }
