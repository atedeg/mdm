package dev.atedeg.mdm.pricing.api

import java.time.LocalDateTime
import java.util.UUID

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*
import cats.syntax.validated
import org.scalatest.*
import org.scalatest.EitherValues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import dev.atedeg.mdm.pricing.api.repositories.*
import dev.atedeg.mdm.pricing.dto.*
import dev.atedeg.mdm.products.dto.*
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.toDTO

trait Mocks:
  val today = LocalDateTime.now
  val future = LocalDateTime.MAX
  val past = LocalDateTime.MIN
  val ricotta = ProductDTO("ricotta", 350)
  val client = ClientDTO(UUID.randomUUID.toDTO)

  val priceListRepository: PriceListRepository = new PriceListRepository:
    override def read[M[_]: Monad: LiftIO]: M[PriceListDTO] = PriceListDTO(
      Map(ricotta -> PriceInEuroCentsDTO(100)),
    ).pure

  val promotionsRepository: PromotionsRepository = new PromotionsRepository:
    override def readByClientID[M[_]: Monad: LiftIO](clientID: String): M[List[PromotionDTO]] =
      val line1 = PromotionLineDTO("fixed", Some(FixedPromotionLineDTO(ricotta, 0.25)), None)
      val line2 = PromotionLineDTO("fixed", Some(FixedPromotionLineDTO(ricotta, 0.75)), None)
      val promotion1 = PromotionDTO(client, future.toDTO, List(line1))
      val promotion2 = PromotionDTO(client, past.toDTO, List(line2))
      List(promotion1, promotion2).pure

  val config: Configuration = Configuration(
    priceListRepository,
    promotionsRepository,
  )

class PriceOrderLineHandler extends AnyWordSpec, Matchers, Mocks:
  "The `priceOrderLineHandler`" should {
    val orderLine = IncomingOrderLineDTO(1, ricotta)
    val action: ServerAction[Configuration, String, PriceInEuroCentsDTO] = handlePriceOrderLine(client.code, orderLine)
    val res = action.unsafeExecute(config)

    "compute the correct date" in {
      res.value shouldBe PriceInEuroCentsDTO(75)
    }
  }
