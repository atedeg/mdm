package dev.atedeg.mdm.clientorders.api

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import cats.syntax.validated
import org.scalatest.EitherValues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.clientorders.api.repositories.*
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

@SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
trait Mocks:
  var savedOrder: Option[InProgressOrderDTO] = None
  var emittedOrderProcessed: List[OrderProcessedDTO] = Nil

  val priceListRepository: PriceListRepository = new PriceListRepository:
    override def read[M[_]: Monad: LiftIO]: M[PriceListDTO] = PriceListDTO(Map(ProductDTO("ricotta", 350) -> 100)).pure

  val orderRepository: OrderRepository = new OrderRepository:
    override def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit] =
      savedOrder = Some(inProgressOrder)
      ().pure

  val emitter: Emitter = new Emitter:
    override def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit] =
      emittedOrderProcessed = orderProcessed :: emittedOrderProcessed
      ().pure

  val config: Configuration = Configuration(priceListRepository, orderRepository, emitter)

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `newOrderHandler`" should {
    val orderLines = List(IncomingOrderLineDTO(10, ProductDTO("ricotta", 350)))
    val customer = CustomerDTO(UUID.randomUUID.toDTO[String], "foo", "IT01088260409")
    val deliveryDate = LocalDateTime.now.toDTO[String]
    val deliveryLocation = LocationDTO(12, 41)
    val orderReceivedDTO = OrderReceivedDTO(orderLines, customer, deliveryDate, deliveryLocation)
    val action: ServerAction[Configuration, String, String] = newOrderHandler(orderReceivedDTO)
    val res = action.unsafeExecute(config)

    "emit all the events" in {
      emittedOrderProcessed match
        case Nil => fail("No events were emitted")
        case List(e) =>
          e.incomingOrder shouldBe IncomingOrderDTO(res.value, orderLines, customer, deliveryDate, deliveryLocation)
        case _ => fail("Emitted more events than expected")
    }

    "save the new in progress order to the DB" in {
      savedOrder match
        case None => fail("The order was not saved in the DB")
        case Some(o) =>
          val incomplete = IncompleteOrderLineDTO(0, 10, ProductDTO("ricotta", 350), 1000)
          val orderLines = List(InProgressOrderLineDTO("incomplete", None, Some(incomplete)))
          o shouldBe InProgressOrderDTO(res.value, orderLines, customer, deliveryDate, deliveryLocation, 1000)
    }
  }
