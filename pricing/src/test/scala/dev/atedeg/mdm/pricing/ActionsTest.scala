package dev.atedeg.mdm.pricing

import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.pricing.utils.*
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.doubleToNumberInOpenClosedRange
import dev.atedeg.mdm.utils.given

trait ClientMock:
  private val clientId: ClientID = ClientID(UUID.randomUUID)
  val client: Client = Client(clientId)

trait PriceListMock:
  val priceList: PriceList = PriceList(
    Map(
      Caciotta(1000) -> 100.euroCents,
    ),
  )

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with ClientMock with PriceListMock:
  Feature("Order pricing") {
    Scenario("Order line from a regular client") {
      Given("an incoming order line")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client is not present")
      When("pricing it")
      val price = priceOrderLine(priceList, List.empty, LocalDateTime.now)(orderLine)
      Then("the price should have no discounts")
      price shouldBe 10000.euroCents
    }

    Scenario("Order line from a client with expired discounts") {
      Given("an incoming order line")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has an expired promotion")
      val promotionLine = PromotionLine.Fixed(Caciotta(1000), 25.0.percent)
      val promotion = Promotion(client, LocalDateTime.MIN, NonEmptyList.of(promotionLine))
      When("pricing the order line")
      val price = priceOrderLine(priceList, List(promotion), LocalDateTime.now)(orderLine)
      Then("the price should have no discount")
      price shouldBe 10000.euroCents
    }

    Scenario("Order line from a client with only a fixed discount") {
      Given("an incoming order line")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has a promotion")
      val promotionLine = PromotionLine.Fixed(Caciotta(1000), 25.0.percent)
      val promotion = Promotion(client, LocalDateTime.MAX, NonEmptyList.of(promotionLine))
      When("pricing the order line")
      val price = priceOrderLine(priceList, List(promotion), LocalDateTime.now)(orderLine)
      Then("the price should have the fixed discount only")
      price shouldBe 7500.euroCents
    }

    Scenario("Order line from a client with only a threshold discount which meets the threshold") {
      Given("an incoming order line which meets the threshold")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has a promotion")
      val promotionLine = PromotionLine.Threshold(Caciotta(1000), 10.threshold, 50.0.percent)
      val promotion = Promotion(client, LocalDateTime.MAX, NonEmptyList.of(promotionLine))
      When("pricing the order line")
      val price = priceOrderLine(priceList, List(promotion), LocalDateTime.now)(orderLine)
      Then("the price should have the correct threshold discount")
      price shouldBe 5500.euroCents
    }

    Scenario("Order line from a client with only a threshold discount which does not meet the threshold") {
      Given("an incoming order line which does not meet the threshold")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has a promotion")
      val promotionLine = PromotionLine.Threshold(Caciotta(1000), 1000.threshold, 50.0.percent)
      val promotion = Promotion(client, LocalDateTime.MAX, NonEmptyList.of(promotionLine))
      When("pricing the order line")
      val price = priceOrderLine(priceList, List(promotion), LocalDateTime.now)(orderLine)
      Then("the price should have no discount")
      price shouldBe 10000.euroCents
    }

    Scenario("Order line from a client with both fixed and threshold discounts") {
      Given("an incoming order line")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has a promotion")
      val promotionLines = NonEmptyList.of(
        PromotionLine.Fixed(Caciotta(1000), 25.0.percent),
        PromotionLine.Threshold(Caciotta(1000), 50.threshold, 50.0.percent),
      )
      val promotion = Promotion(client, LocalDateTime.MAX, promotionLines)
      When("pricing the order line")
      val price = priceOrderLine(priceList, List(promotion), LocalDateTime.now)(orderLine)
      Then("the price should have both discounts applied")
      price shouldBe 5625.euroCents
    }

    Scenario("Order line from a client with multiple discounts") {
      Given("an incoming order line")
      val orderLine = 100 of Caciotta(1000)
      And("a promotions list where the client has a promotion")
      val promotionLines1 = NonEmptyList.of(
        PromotionLine.Fixed(Caciotta(1000), 25.0.percent),
        PromotionLine.Threshold(Caciotta(1000), 50.threshold, 50.0.percent),
      )
      val promotionLines2 = NonEmptyList.of(
        PromotionLine.Threshold(Caciotta(1000), 75.threshold, 60.0.percent),
      )
      val activePromotion1 = Promotion(client, LocalDateTime.MAX, promotionLines1)
      val activePromotion2 = Promotion(client, LocalDateTime.MAX, promotionLines2)
      val expiredPromotion = Promotion(client, LocalDateTime.MIN, promotionLines1)
      val promotions = List(activePromotion1, activePromotion2, expiredPromotion)
      When("pricing the order line")
      val price = priceOrderLine(priceList, promotions, LocalDateTime.now)(orderLine)
      Then("the price should have all discounts correctly applied")
      price shouldBe 5063.euroCents
    }
  }
