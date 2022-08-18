package dev.atedeg.mdm.pricing.api.endpoints

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter

import dev.atedeg.mdm.pricing.api.*
import dev.atedeg.mdm.pricing.api.repositories.*
import dev.atedeg.mdm.pricing.dto.*
import dev.atedeg.mdm.products.dto.*
import dev.atedeg.mdm.utils.monads.*

object PricingEndpoints:
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val priceOrderLineEndpoint: PublicEndpoint[(String, Int, String, Int), String, PriceInEuroCentsDTO, Any] =
    endpoint.get
      .in("price")
      .in(query[String]("clientID").description("The client ID"))
      .in(query[Int]("quantity").description("The quantity of the product to be priced"))
      .in(query[String]("cheeseType").description("The type of cheese in the order line to be priced"))
      .in(query[Int]("weight").description("The weight of the cheese in the order line to be priced"))
      .out(jsonBody[PriceInEuroCentsDTO].description("The price of the order line"))
      .errorOut(stringBody)
      .description("Gets the price of an order line for a given client")

  val priceOrderLineRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    priceOrderLineEndpoint.serverLogic { (clientID, quantity, cheeseType, weight) =>
      val product = ProductDTO(cheeseType, weight)
      val orderLine = IncomingOrderLineDTO(quantity, product)
      val action: ServerAction[Configuration, String, PriceInEuroCentsDTO] = priceOrderLineHandler(clientID, orderLine)
      action.value.run(Configuration(PriceListRepositoryDB("foo"), PromotionsRepositoryDB("bar")))
    },
  )
