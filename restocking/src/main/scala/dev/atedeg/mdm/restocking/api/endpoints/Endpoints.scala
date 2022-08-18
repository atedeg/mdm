package dev.atedeg.mdm.restocking.api.endpoints

import cats.data.{ EitherT, ReaderT }
import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter

import dev.atedeg.mdm.restocking.api.dto.RemainingMilkDTO
import dev.atedeg.mdm.restocking.api.remaningQuintalsOfMilkHandler
import dev.atedeg.mdm.restocking.api.repositories.{ DBStockRepository, StockRepository }
import dev.atedeg.mdm.utils.monads.ServerAction

object RemainingQuintalsOfMilkEndpoint:
  private val handler: ServerAction[StockRepository, String, RemainingMilkDTO] = remaningQuintalsOfMilkHandler

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val remainingQuintalsOfMilkEndpoint: PublicEndpoint[Unit, String, RemainingMilkDTO, Any] =
    endpoint.get
      .in("milk")
      .out(jsonBody[RemainingMilkDTO].description("The quintals of milk remaining in stock"))
      .errorOut(stringBody)
      .description("Gets the remaining quintals of milk.")

  val remainingQuintalsOfMilkRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    remainingQuintalsOfMilkEndpoint.serverLogic(_ => handler.value.run(DBStockRepository("conn-string"))),
  )
