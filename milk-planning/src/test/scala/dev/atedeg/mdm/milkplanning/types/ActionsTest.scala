package dev.atedeg.mdm.milkplanning.types

import java.time.LocalDateTime

import cats.data.{ NonEmptyList, Writer }
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.OrderMilk
import dev.atedeg.mdm.products.CheeseType
import dev.atedeg.mdm.products.Grams
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.*

trait Fixture {

  val recipeBook: RecipeBook = Map(
    CheeseType.Squacquerone -> QuintalsOfMilk(1),
    CheeseType.Casatella -> QuintalsOfMilk(1),
    CheeseType.Ricotta -> QuintalsOfMilk(1),
    CheeseType.Stracchino -> QuintalsOfMilk(1),
    CheeseType.Caciotta -> QuintalsOfMilk(1),
  )
}

class ActionsTest extends AnyFeatureSpec with GivenWhenThen with Matchers with Fixture {

  Feature("Estimate the quintals of milk needed for the following week") {
    Scenario("Raffaella wants to estimate the quintals of milk") {
      Given("the quintals of milk of the previous year for the same period")
      And("a list of products to be produced")
      And("an empty stock")
      And("no milk in stock")
      val qomPreviousYear = QuintalsOfMilk(4)
      val requestedProducts = NonEmptyList.of(
        RequestedProduct(Squacquerone(Grams(100)), Quantity(50), LocalDateTime.now()),
      )
      val currentStock: Stock = _ => StockedQuantity(0)
      val stockedMilk = QuintalsOfMilk(0)
      When("the estimation is ready to be computed")
      val estimatorMonad: Writer[List[OrderMilk], QuintalsOfMilk] = estimateQuintalsOfMilk(
        qomPreviousYear,
        requestedProducts,
        currentStock,
        recipeBook,
        stockedMilk,
      )
      val (events, estimation) = estimatorMonad.run
      println(estimation)

      Then("the result will be in the interval [..., ...]")

    }
  }
}
