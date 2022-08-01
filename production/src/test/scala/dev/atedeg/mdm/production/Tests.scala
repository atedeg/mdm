package dev.atedeg.mdm.production

import cats.data.NonEmptyList
import dev.atedeg.mdm.products.{CheeseType, Product}

import java.util.UUID
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.EitherValues.*
import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.utils.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.doubleToPositiveDecimal
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.monads.given
import Ingredient.*
import OutgoingEvent.*

extension (n: PositiveNumber)
  def ofProd(p: Product): ProductionPlanItem = ProductionPlanItem(p, NumberOfUnits(n))

trait Mocks {
  val productionPlan: ProductionPlan = ProductionPlan(NonEmptyList.of(
    10_000 ofProd Product.Caciotta(500),
    10_000 ofProd Product.Caciotta(1000),
    10_000 ofProd Product.Ricotta(350),
  ))
  val List(
    caciotta500Production,
    caciotta1000Production,
    ricotta350Production,
  ) = setupProductions(productionPlan).toList
  val recipeBook: RecipeBook = Map(
    CheeseType.Caciotta -> Recipe(NonEmptyList.of(
      10 of Milk,
      10 of Cream,
      10 of Rennet,
      10 of Salt,
      10 of Probiotics,
    ))
  ).get
}

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

  Feature("Production management") {
    Scenario("A production is started") {
      Given("a production that has to be started")
      val production = caciotta500Production
      When("it is started")
      val startAction: Action[MissingRecipe, StartProduction, Production.InProgress] =
        startProduction(recipeBook)(production)
      val (events, result) = startAction.execute
      Then("an event is emitted to notify that the production should start")
      And("the correct amount of products is computed")
      val expectedIngredients = NonEmptyList.of[QuintalsOfIngredient](
        500 of Milk,
        500 of Cream,
        500 of Rennet,
        500 of Salt,
        500 of Probiotics,
      )
      events should contain(StartProduction(expectedIngredients))
      And("the production is started")
      result.value shouldBe Production.InProgress(production.ID, production.productToProduce, production.unitsToProduce)
    }

    Scenario("A production is started with no recipe") {
      Given("a production that has to be started")
      val production = caciotta1000Production
      And("has no recipe")
      val recipeBook = Map[CheeseType, Recipe]()
      When("it is started")
      val startAction: Action[MissingRecipe, StartProduction, Production.InProgress] =
        startProduction(recipeBook.get)(production)
      val (events, result) = startAction.execute
      Then("an error is raised")
      result.left.value shouldBe MissingRecipe(CheeseType.Caciotta)
      And("no production is started")
      events shouldBe empty
    }

    Scenario("A production is ended") {
      Given("a production that is in progress")
      val production: Production.InProgress = Production.InProgress(
        ricotta350Production.ID,
        ricotta350Production.productToProduce,
        ricotta350Production.unitsToProduce,
      )
      When("it is ended")
      val endAction: SafeAction[ProductionEnded, Production.Ended] = endProduction(production)
      val (events, result) = endAction.execute
      Then("it should emit an event to notify that the production ended")
      result shouldBe a[Production.Ended]
      result.ID shouldBe production.ID
      result.producedUnits shouldBe production.unitsInProduction
      result.producedProduct shouldBe production.productInProduction
      events should contain(ProductionEnded(result.ID, result.batchID))
    }
  }
}
