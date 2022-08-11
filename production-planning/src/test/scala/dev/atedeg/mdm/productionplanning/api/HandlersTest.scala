package dev.atedeg.mdm.productionplanning.api

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.productionplanning.api.acl.{ CustomerDTO, IncomingOrderDTO, IncomingOrderLineDTO, LocationDTO }
import dev.atedeg.mdm.productionplanning.api.repositories.ReceivedOrderRepository
import dev.atedeg.mdm.productionplanning.dto.{ OrderDTO, OrderedProductDTO }
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

trait Mocks:
  @SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
  var orders: List[OrderDTO] = Nil
  val receivedOrderRepository: ReceivedOrderRepository = new ReceivedOrderRepository:
    override def saveNewOrder[M[_]: Monad: LiftIO](order: OrderDTO): M[Unit] =
      orders = order :: orders
      ().pure

    override def getOrders[M[_]: Monad: LiftIO]: M[List[OrderDTO]] = ???

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `handleOrderReceived`" should {
    "save the new order in the DB" in {
      val product = ProductDTO("caciotta", 500)
      val date = LocalDateTime.now().toDTO[String]
      val orderLines = List(IncomingOrderLineDTO(10, product))
      val id = UUID.randomUUID().toDTO[String]
      val incomingOrder =
        IncomingOrderDTO(id, orderLines, CustomerDTO(id, "Pippo", "IT12345678910"), date, LocationDTO(12.6, 44.6))
      val action: ServerAction[ReceivedOrderRepository, String, Unit] = handleOrderReceived(incomingOrder)
      action.unsafeExecute(receivedOrderRepository)
      val localDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate
      orders should contain(OrderDTO(id, localDate.toDTO[String], List(OrderedProductDTO(product, 10))))
    }
  }
