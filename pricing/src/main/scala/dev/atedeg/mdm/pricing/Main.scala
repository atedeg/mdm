package dev.atedeg.mdm.pricing

import scala.util.Properties

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.slf4j.loggerFactoryforSync
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import dev.atedeg.mdm.pricing.api.endpoints.PricingEndpoints.*

object Main extends IOApp:
  private val swaggerEndpoint = SwaggerInterpreter().fromEndpoints[IO](
    priceOrderLineEndpoint :: Nil,
    "Pricing",
    Properties.envOrElse("VERSION", "v1-beta"),
  )

  private val swaggerRoute = Http4sServerInterpreter[IO]().toRoutes(swaggerEndpoint)
  private val routes: HttpRoutes[IO] = priceOrderLineRoute <+> swaggerRoute

  implicit val logging: LoggerFactory[IO] = Slf4jFactory[IO]
  private val logger: SelfAwareStructuredLogger[IO] = LoggerFactory[IO].getLogger

  override def run(args: List[String]): IO[ExitCode] = BlazeServerBuilder[IO]
    .bindHttp(Properties.envOrElse("PORT", "8080").toInt, Properties.envOrElse("HOST", "localhost"))
    .withHttpApp(Router("/" -> routes).orNotFound)
    .resource
    .use(_ => logger.info("server started") >> IO.never[Unit])
    .as(ExitCode.Success)
