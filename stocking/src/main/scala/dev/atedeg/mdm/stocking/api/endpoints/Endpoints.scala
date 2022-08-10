package dev.atedeg.mdm.stocking.api.endpoints

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import sttp.tapir.*
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter

import dev.atedeg.mdm.stocking.api.*
import dev.atedeg.mdm.stocking.api.repositories.{
  BatchesRepository,
  BatchesRepositoryDB,
  StockRepository,
  StockRepositoryDB,
}
import dev.atedeg.mdm.stocking.dto.{ AvailableStockDTO, DesiredStockDTO }
import dev.atedeg.mdm.utils.monads.ServerAction

object StockRequests:
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val desiredStockRequestEndpoint: PublicEndpoint[Unit, String, DesiredStockDTO, Any] =
    endpoint.get
      .in("stock" / "desired")
      .out(jsonBody[DesiredStockDTO].description("The products missing from the stock"))
      .errorOut(stringBody)

  val desiredStockRoute: HttpRoutes[IO] =
    val handler: ServerAction[StockRepository, String, DesiredStockDTO] = handleDesiredStockRequest
    Http4sServerInterpreter[IO]().toRoutes(
      desiredStockRequestEndpoint.serverLogic(_ => handler.value.run(StockRepositoryDB("conn-string"))),
    )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val availableStockRequestEndpoint: PublicEndpoint[Unit, String, AvailableStockDTO, Any] =
    endpoint.get
      .in("stock")
      .out(jsonBody[AvailableStockDTO].description("The current stock"))
      .errorOut(stringBody)

  val availableStockRoute: HttpRoutes[IO] =
    val handler: ServerAction[StockRepository, String, AvailableStockDTO] = handleProductsInStockRequest
    Http4sServerInterpreter[IO]().toRoutes(
      availableStockRequestEndpoint.serverLogic(_ => handler.value.run(StockRepositoryDB("conn-string"))),
    )

object BatchesRequests:
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val approveBatchRequestEndpoint: PublicEndpoint[String, String, Unit, Any] =
    endpoint.post
      .in("batch" / "approve")
      .in(stringBody.description("The id of the batch to approve"))
      .errorOut(stringBody)

  val approveBatchRoute: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      approveBatchRequestEndpoint.serverLogic { request =>
        val handler: ServerAction[BatchesRepository, String, Unit] = approveBatchHandler(request)
        handler.value.run(BatchesRepositoryDB("conn-string"))
      },
    )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val rejectBatchRequestEndpoint: PublicEndpoint[String, String, Unit, Any] =
    endpoint.post
      .in("batch" / "reject")
      .in(stringBody.description("The id of the batch to reject"))
      .errorOut(stringBody)

  val rejectBatchRoute: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      rejectBatchRequestEndpoint.serverLogic { request =>
        val handler: ServerAction[BatchesRepository, String, Unit] = rejectBatchHandler(request)
        handler.value.run(BatchesRepositoryDB("conn-string"))
      },
    )
