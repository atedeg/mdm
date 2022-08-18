package dev.atedeg.mdm.clientorders.api

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import cats.syntax.validated
import org.scalatest.*
import org.scalatest.EitherValues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.clientorders.InProgressOrder
import dev.atedeg.mdm.clientorders.api.repositories.*
import dev.atedeg.mdm.clientorders.api.services.PriceOrderLineService
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

@SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
trait Mocks:
  var emittedProductPalletized: List[ProductPalletizedDTO] = Nil
  var emittedOrderProcessed: List[OrderProcessedDTO] = Nil
  var savedOrder: Option[InProgressOrderDTO] = None

  val incompleteOrderLine: IncompleteOrderLineDTO =
    IncompleteOrderLineDTO(0, 100, ProductDTO("ricotta", 350), PriceInEuroCentsDTO(1000))
  val oldInProgressOrder: InProgressOrderDTO = InProgressOrderDTO(
    UUID.randomUUID.toDTO,
    List(InProgressOrderLineDTO("Incomplete", None, Some(incompleteOrderLine))),
    ClientDTO(UUID.randomUUID.toDTO, "foo", "IT01088260409"),
    LocalDateTime.now.toDTO,
    LocationDTO(12, 42),
    PriceInEuroCentsDTO(1000),
  )

  val orderRepository: OrderRepository = new OrderRepository:
    override def writeInProgressOrder[M[_]: Monad: LiftIO](inProgressOrder: InProgressOrderDTO): M[Unit] =
      savedOrder = Some(inProgressOrder)
      ().pure
    override def readInProgressOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[InProgressOrderDTO] =
      oldInProgressOrder.pure
    override def readCompletedOrder[M[_]: Monad: LiftIO: CanRaise[String]](orderID: String): M[CompletedOrderDTO] = ???
    override def updateOrderToCompleted[M[_]: Monad: LiftIO: CanRaise[String]](order: CompletedOrderDTO): M[Unit] = ???

  val emitter: Emitter = new Emitter:
    override def emitOrderProcessed[M[_]: Monad: LiftIO](orderProcessed: OrderProcessedDTO): M[Unit] =
      emittedOrderProcessed = orderProcessed :: emittedOrderProcessed
      ().pure
    override def emitProductPalletized[M[_]: Monad: LiftIO](productPalletized: ProductPalletizedDTO): M[Unit] =
      emittedProductPalletized = productPalletized :: emittedProductPalletized
      ().pure

  val priceOrderLineService: PriceOrderLineService = new PriceOrderLineService:
    override def getOrderLinePrice[M[_]: Monad: LiftIO](
        clientID: String,
        orderLine: IncomingOrderLineDTO,
    ): M[PriceInEuroCentsDTO] = PriceInEuroCentsDTO(100 * orderLine.quantity).pure

  val config: Configuration = Configuration(priceOrderLineService, orderRepository, emitter)

class NewOrderHandler extends AnyWordSpec, Matchers, Mocks:
  "The `newOrderHandler`" should {
    val orderLines = List(IncomingOrderLineDTO(10, ProductDTO("ricotta", 350)))
    val client = ClientDTO(UUID.randomUUID.toDTO[String], "foo", "IT01088260409")
    val deliveryDate = LocalDateTime.now.toDTO[String]
    val deliveryLocation = LocationDTO(12, 41)
    val orderReceivedDTO = OrderReceivedDTO(orderLines, client, deliveryDate, deliveryLocation)
    val action: ServerAction[Configuration, String, String] = newOrderHandler(orderReceivedDTO)
    val res = action.unsafeExecute(config)

    "emit all the events" in {
      emittedOrderProcessed match
        case Nil => fail("No events were emitted")
        case List(e) =>
          e.incomingOrder shouldBe IncomingOrderDTO(res.value, orderLines, client, deliveryDate, deliveryLocation)
        case _ => fail("Emitted more events than expected")
    }

    "save the new in progress order to the DB" in {
      savedOrder match
        case None => fail("The order was not saved in the DB")
        case Some(o) =>
          val incomplete = IncompleteOrderLineDTO(0, 10, ProductDTO("ricotta", 350), PriceInEuroCentsDTO(1000))
          val orderLines = List(InProgressOrderLineDTO("Incomplete", None, Some(incomplete)))
          o shouldBe InProgressOrderDTO(
            res.value,
            orderLines,
            client,
            deliveryDate,
            deliveryLocation,
            PriceInEuroCentsDTO(1000),
          )
    }
  }

class ProductPalletizedForOrderHandler extends AnyWordSpec, Matchers, Mocks:
  "The `ProductPalletizedForOrderHandler`" should {
    val dto = ProductPalletizedForOrderDTO(oldInProgressOrder.id, 10, ProductDTO("ricotta", 350))
    val action: ServerAction[Configuration, String, Unit] = productPalletizedForOrderHandler(dto)
    action.unsafeExecute(config)

    "save the updated order" in {
      savedOrder match
        case None => fail("The order was not updated")
        case Some(o) =>
          val orderLine = o.orderLines(0)
          orderLine.incompleteDTO match
            case None => fail("The order was not updated correctly")
            case Some(i) => i.actual shouldBe 10
    }

    "emit all the events" in {
      emittedProductPalletized match
        case Nil => fail("No events were emitted")
        case List(e) =>
          e.product shouldBe ProductDTO("ricotta", 350)
          e.quantity shouldBe 10
        case _ => fail("Emitted more events than expected")
    }
  }
