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
import dev.atedeg.mdm.clientorders.dto.*
import dev.atedeg.mdm.utils.monads.*

object OrdersEndpoint:
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val newOrderEndpoint: PublicEndpoint[OrderReceivedDTO, String, String, Any] =
    endpoint.post
      .in("order")
      .in(jsonBody[OrderReceivedDTO].description("The order that needs to be placed"))
      .out(stringBody.description("The ID assigned to the placed order"))
      .errorOut(stringBody)

  val newOrderRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    newOrderEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, String] = newOrderHandler(o)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), OrderRepositoryDB("bar"), EmitterMQ()))
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val palletizeProductForOrder: PublicEndpoint[ProductPalletizedForOrderDTO, String, Unit, Any] =
    endpoint.post
      .in("order" / "palletize")
      .in(jsonBody[ProductPalletizedForOrderDTO].description("The product and quantity palletized for the given order"))
      .errorOut(stringBody)

  val palletizeProductRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    palletizeProductForOrder.serverLogic { p =>
      val action: ServerAction[Configuration, String, Unit] = productPalletizedForOrderHandler(p)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), OrderRepositoryDB("bar"), EmitterMQ()))
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val orderCompletedEndpoint: PublicEndpoint[OrderCompletedDTO, String, Unit, Any] =
    endpoint.post
      .in("order" / "complete")
      .in(jsonBody[OrderCompletedDTO].description("The ID of the order that has been completed"))
      .errorOut(stringBody)

  val orderCompletedRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    orderCompletedEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, Unit] = orderCompletedHandler(o)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), OrderRepositoryDB("bar"), EmitterMQ()))
    },
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val getTransportDocumentEndpoint: PublicEndpoint[String, String, TransportDocumentDTO, Any] =
    endpoint.get
      .in("order")
      .in(path[String].description("The ID of the order for which the transport document is requested"))
      .in("ddt")
      .out(jsonBody[TransportDocumentDTO].description("The transport document for the given order"))
      .errorOut(stringBody)

  val getTransportDocumentRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    getTransportDocumentEndpoint.serverLogic { o =>
      val action: ServerAction[Configuration, String, TransportDocumentDTO] = getTransportDocumentHandler(o)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), OrderRepositoryDB("bar"), EmitterMQ()))
    },
  )
