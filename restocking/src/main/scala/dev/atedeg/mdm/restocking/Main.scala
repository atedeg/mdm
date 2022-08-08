package dev.atedeg.mdm.restocking

import scala.concurrent.ExecutionContext
import scala.util.Properties

import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import dev.atedeg.mdm.restocking.api.endpoints.RemainingQuintalsOfMilkEndpoint

object Main extends IOApp:
  private val swaggerEndpoint = SwaggerInterpreter().fromEndpoints[IO](
    RemainingQuintalsOfMilkEndpoint.remainingQuintalsOfMilkEndpoint :: Nil,
    "Restocking",
    Properties.envOrElse("VERSION", "v1-beta"),
  )
  private val swaggerRoute = Http4sServerInterpreter[IO]().toRoutes(swaggerEndpoint)

  private val routes: HttpRoutes[IO] = RemainingQuintalsOfMilkEndpoint.remainingQuintalsOfMilkRoute <+> swaggerRoute

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(Properties.envOrElse("PORT", "8080").toInt, Properties.envOrElse("HOST", "localhost"))
      .withHttpApp(Router("/" -> routes).orNotFound)
      .resource
      .use(_ => IO.println("Started") >> IO.never[Unit])
      .as(ExitCode.Success)
