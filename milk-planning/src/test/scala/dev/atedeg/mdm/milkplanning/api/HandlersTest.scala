package dev.atedeg.mdm.milkplanning.api

import java.time.LocalDateTime
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.milkplanning.QuintalsOfMilk
import dev.atedeg.mdm.milkplanning.api.acl.{ CustomerDTO, IncomingOrderDTO, IncomingOrderLineDTO, LocationDTO }
import dev.atedeg.mdm.milkplanning.api.emitters.OrderMilkEmitter
import dev.atedeg.mdm.milkplanning.api.repositories.{ ReceivedOrderRepository, RecipeBookRepository }
import dev.atedeg.mdm.milkplanning.dto.{ OrderMilkDTO, RecipeBookDTO, RequestedProductDTO }
import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

trait Mocks:
  @SuppressWarnings(Array("org.wartremover.warts.Var", "scalafix:DisableSyntax.var"))
  var orderHistory: List[RequestedProductDTO] = Nil
  private val orderMilkEmitter: OrderMilkEmitter = new OrderMilkEmitter:
    override def emit[M[_]: Monad: LiftIO](orderMilkDTO: OrderMilkDTO): M[Unit] = ???
  val receivedOrderRepository: ReceivedOrderRepository = new ReceivedOrderRepository:
    override def save[M[_]: Monad: LiftIO](requestedProducts: List[RequestedProductDTO]): M[Unit] =
      orderHistory = requestedProducts
      ().pure
    override def getRequestedProducts[M[_]: Monad: LiftIO]: M[List[RequestedProductDTO]] = ???
  private val recipeBookRepository: RecipeBookRepository = new RecipeBookRepository:
    override def getRecipeBook[M[_]: Monad: LiftIO]: M[RecipeBookDTO] = ???
  val config: Configuration = Configuration(receivedOrderRepository, recipeBookRepository, orderMilkEmitter)

class HandlersTest extends AnyWordSpec, Matchers, Mocks:
  "The `receivedOrderHandler`" should {
    "save the received order in the DB" in {
      val product = ProductDTO("squacquerone", 250)
      val date = LocalDateTime.now().toDTO[String]
      val orderLines = List(IncomingOrderLineDTO(50, product))
      val uuid = UUID.randomUUID().toDTO[String]
      val incomingOrder =
        IncomingOrderDTO(uuid, orderLines, CustomerDTO(uuid, "Foo", "IT01088260409"), date, LocationDTO(12.6, 44.6))
      val action: ServerAction[ReceivedOrderRepository, String, Unit] = receivedOrderHandler(incomingOrder)
      action.unsafeExecute(receivedOrderRepository)
      orderHistory should contain(RequestedProductDTO(product, 50, date))
    }
  }
