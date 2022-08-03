package dev.atedeg.mdm.restocking

import cats.data.NonEmptyList
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.products.{ Ingredient, Product }
import dev.atedeg.mdm.products.Ingredient.{ Cream, Milk, Probiotics, Rennet, Salt }
import dev.atedeg.mdm.utils.given

trait Mocks {
  private val milk = QuintalsOfIngredient(WeightInQuintals(5.5), Milk)
  private val cream = QuintalsOfIngredient(WeightInQuintals(3.0), Cream)
  private val rennet = QuintalsOfIngredient(WeightInQuintals(2.0), Rennet)
  private val salt = QuintalsOfIngredient(WeightInQuintals(3.0), Salt)
  private val probiotics = QuintalsOfIngredient(WeightInQuintals(0.1), Probiotics)

  val ingredients: NonEmptyList[QuintalsOfIngredient] = NonEmptyList.of(milk, cream, rennet, salt, probiotics)

}

class ActionsTest extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

  Feature("Consume ingredients") {
    Scenario("Some ingredients are consumed as a result of the producion process") {
      Given("An available stock")
      val stock: Stock = Map(
        Milk -> StockedQuantity(10.0),
        Cream -> StockedQuantity(20.0),
        Rennet -> StockedQuantity(30.0),
        Salt -> StockedQuantity(40.0),
        Probiotics -> StockedQuantity(50.0),
      )
      When("Some ingredients are consumed")
      val newStock = consumeIngredients(stock)(ingredients)
      Then("the stock should be consequently updated")
      val updatedStock = Map(
        Milk -> StockedQuantity(4.5),
        Cream -> StockedQuantity(17.0),
        Rennet -> StockedQuantity(28.0),
        Salt -> StockedQuantity(37.0),
        Probiotics -> StockedQuantity(49.9),
      )
      newStock shouldBe updatedStock
    }
  }
}
