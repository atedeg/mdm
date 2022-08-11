package dev.atedeg.mdm.milkplanning

import java.time.LocalDateTime

import cats.data.{ NonEmptyList, NonEmptyMap, Writer }
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.milkplanning.*
import dev.atedeg.mdm.milkplanning.OutgoingEvent.OrderMilk
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.*
import dev.atedeg.mdm.milkplanning.utils.QuintalsOfMilkOps.given
import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

trait Mocks:

  val recipeBook: RecipeBook = Map(
    CheeseType.Squacquerone -> Yield(5.55),
    CheeseType.Casatella -> Yield(5.55),
    CheeseType.Ricotta -> Yield(4.54),
    CheeseType.Stracchino -> Yield(6.55),
    CheeseType.Caciotta -> Yield(8.33),
  )

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class ActionsTest extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks:

  Feature("Estimate the quintals of milk needed for the following week") {
    Scenario("Raffaella wants to estimate the quintals of milk") {
      Given("the quintals of milk of the previous year for the same period")
      val qomPreviousYear = 12.quintalsOfMilk
      And("a list of products to be produced")
      val requestedProducts = List(
        RequestedProduct(Squacquerone(100), Quantity(500), LocalDateTime.now()),
        RequestedProduct(Squacquerone(250), Quantity(300), LocalDateTime.now()),
        RequestedProduct(Ricotta(350), Quantity(50), LocalDateTime.now()),
        RequestedProduct(Caciotta(500), Quantity(100), LocalDateTime.now()),
      )
      And("an empty stock")
      val currentStock: Stock = Map.empty.withDefaultValue(StockedQuantity(0))
      And("there is no milk in stock")
      val stockedMilk = QuintalsOfMilk(0)
      When("estimating the necessary quintals of milk")
      val estimateAction: SafeAction[OrderMilk, QuintalsOfMilk] = estimateQuintalsOfMilk(
        qomPreviousYear,
        requestedProducts,
        currentStock,
        recipeBook,
        stockedMilk,
      )
      val (events, estimation) = estimateAction.execute
      Then("the result should be at least greater than the quintals of milk needed to produce all products")
      val quintalsForRequestedProducts =
        requestedProducts.map { case RequestedProduct(Product(_, weight), quantity, _) => weight.n * quantity.n }
          .map(_.toDecimal.toNonNegative / 100_000)
          .map(_.ceil.quintalsOfMilk)
          .foldLeft(0.quintalsOfMilk)(_ + _)
      estimation should be > quintalsForRequestedProducts
      events should not be empty
      events.map(_.quintalsOfMilk) should contain(estimation)
    }
  }
