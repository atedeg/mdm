package dev.atedeg.mdm.clientorders.api.endpoints

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter

import dev.atedeg.mdm.clientorders.api.*
import dev.atedeg.mdm.clientorders.api.repositories.*
import dev.atedeg.mdm.clientorders.api.services.*
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.products.dto.*
import dev.atedeg.mdm.utils.monads.*

object OrdersEndpoints:
  private val priceOrderLineService = PriceOrderLineServiceHTTP()
  private val orderRepository = OrderRepositoryDB("bar")
  private val emitter = EmitterMQ()
  private val configuration = Configuration(priceOrderLineService, orderRepository, emitter)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val newOrderEndpoint: PublicEndpoint[OrderReceivedDTO, String, String, Any] =
    endpoint.post
      .in("order")
      .in(jsonBody[OrderReceivedDTO].description("The order that needs to be placed"))
      .out(stringBody.description("The ID assigned to the placed order"))
      .errorOut(stringBody)
      .description("Creates a new order.")

  val newOrderRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    newOrderEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, String] = newOrderHandler(o)
      action.value.run(configuration)
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val palletizeProductForOrderEndpoint: PublicEndpoint[(String, ProductWithQuantityDTO), String, Unit, Any] =
    endpoint.put
      .in("order")
      .in(
        path[String]
          .description("The ID of the order for which the product needs to be palletized")
          .name("order-id"),
      )
      .in("palletize")
      .in(jsonBody[ProductWithQuantityDTO].description("The product and quantity palletized for the given order"))
      .errorOut(stringBody)
      .description("Palletize a product for the given order.")

  val palletizeProductForOrderRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    palletizeProductForOrderEndpoint.serverLogic { case (orderID, ProductWithQuantityDTO(quantity, product)) =>
      val action: ServerAction[Configuration, String, Unit] =
        productPalletizedForOrderHandler(ProductPalletizedForOrderDTO(orderID, quantity, product))
      action.value.run(configuration)
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val orderCompletedEndpoint: PublicEndpoint[String, String, Unit, Any] =
    endpoint.put
      .in("order")
      .in(
        path[String]
          .description("The ID of the order that has been completed")
          .name("order-id"),
      )
      .in("complete")
      .errorOut(stringBody)

  val orderCompletedRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    orderCompletedEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, Unit] = orderCompletedHandler(OrderCompletedDTO(o))
      action.value.run(configuration)
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val getTransportDocumentEndpoint: PublicEndpoint[String, String, TransportDocumentDTO, Any] =
    endpoint.get
      .in("order")
      .in(
        path[String]
          .description("The ID of the order for which the transport document is requested")
          .name("order-id"),
      )
      .in("ddt")
      .out(jsonBody[TransportDocumentDTO].description("The transport document for the given order"))
      .errorOut(stringBody)

  val getTransportDocumentRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    getTransportDocumentEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, TransportDocumentDTO] = getTransportDocumentHandler(o)
      action.value.run(configuration)
    },
  )
