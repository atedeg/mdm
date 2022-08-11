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
      .in(jsonBody[OrderReceivedDTO].description("TODO"))
      .out(stringBody)
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
      .in(jsonBody[ProductPalletizedForOrderDTO].description("TODO"))
      .errorOut(stringBody)

  val palletizeProductRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    palletizeProductForOrder.serverLogic { p =>
      val action: ServerAction[Configuration, String, Unit] = productPalletizedForOrderHandler(p)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), OrderRepositoryDB("bar"), EmitterMQ()))
    },
  )
