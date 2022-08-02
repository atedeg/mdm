package dev.atedeg.mdm.production

import java.util.UUID

import OutgoingEvent.*
import cats.data.NonEmptyList
import org.scalatest.EitherValues.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.utils.*
import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.Ingredient.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.doubleToPositiveDecimal
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.monads.given

extension (n: PositiveNumber) def ofProd(p: Product): ProductionPlanItem = ProductionPlanItem(p, NumberOfUnits(n))

trait Mocks {
  private val productionID = ProductionID(UUID.randomUUID)
  val production: Production.ToStart = Production.ToStart(productionID, Product.Caciotta(500), NumberOfUnits(10_000))
  val allIngredients: NonEmptyList[Ingredient] = NonEmptyList.of(Milk, Cream, Rennet, Salt, Probiotics)
}

@SuppressWarnings(Array("scalafix:DisableSyntax.noValPatterns"))
class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

  Feature("Production management") {
    Scenario("A production plan is handled") {
      Given("A production plan")
      val productionPlan = ProductionPlan(
        NonEmptyList.of(
          10_000 ofProd Product.Caciotta(500),
          20_000 ofProd Product.Caciotta(1000),
        ),
      )
      When("it is used to setup the productions")
      val productions = setupProductions(productionPlan)
      Then("the final productions should match the plan's ones")
      val List(p1, p2) = productions.toList
      (p1.unitsToProduce, p1.productToProduce) shouldBe (NumberOfUnits(10_000), Product.Caciotta(500))
      (p2.unitsToProduce, p2.productToProduce) shouldBe (NumberOfUnits(20_000), Product.Caciotta(1000))
    }

    Scenario("A production is started") {
      Given("a production that has to be started")
      And("a recipe book")
      val recipeBook = Map(CheeseType.Caciotta -> Recipe(allIngredients.map(10 of _))).get
      When("it is started")
      val startAction: Action[MissingRecipe, StartProduction, Production.InProgress] =
        startProduction(recipeBook)(production)
      val (events, result) = startAction.execute
      Then("an event is emitted to notify that the production should start")
      And("the correct amount of products is computed")
      val expectedIngredients = allIngredients.map(500 of _)
      events should contain(StartProduction(expectedIngredients))
      And("the production is started")
      result.value shouldBe Production.InProgress(production.ID, production.productToProduce, production.unitsToProduce)
    }

    Scenario("A production is started with no recipe") {
      Given("a production that has to be started")
      And("has no recipe")
      val emptyRecipeBook = Map[CheeseType, Recipe]().get
      When("it is started")
      val startAction: Action[MissingRecipe, StartProduction, Production.InProgress] =
        startProduction(emptyRecipeBook)(production)
      val (events, result) = startAction.execute
      Then("an error is raised")
      result.left.value shouldBe MissingRecipe(CheeseType.Caciotta)
      And("no production is started")
      events shouldBe empty
    }

    Scenario("A production is ended") {
      Given("a production that is in progress")
      val productionInProgress: Production.InProgress = Production.InProgress(
        production.ID,
        production.productToProduce,
        production.unitsToProduce,
      )
      When("it is ended")
      val endAction: SafeAction[ProductionEnded, Production.Ended] = endProduction(productionInProgress)
      val (events, result) = endAction.execute
      Then("it should emit an event to notify that the production ended")
      result shouldBe a[Production.Ended]
      result.ID shouldBe productionInProgress.ID
      result.producedUnits shouldBe productionInProgress.unitsInProduction
      result.producedProduct shouldBe productionInProgress.productInProduction
      events should contain(ProductionEnded(result.ID, result.batchID))
    }
  }
}
