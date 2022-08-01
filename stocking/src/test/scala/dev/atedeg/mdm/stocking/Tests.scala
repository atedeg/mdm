package dev.atedeg.mdm.stocking

import java.util.UUID

import cats.data.{ Writer, WriterT }
import org.scalatest.EitherValues.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.products.utils.*
import dev.atedeg.mdm.stocking.Errors.*
import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.monads.*

trait Mocks {
  val batchID: BatchID = BatchID(UUID.randomUUID())
  val cheeseType: CheeseType = CheeseType.Squacquerone
  val squacquerone: Product = Product.Squacquerone(100)
  val casatella: Product = Product.Casatella(300)
  val ricotta: Product = Product.Ricotta(350)
  val stracchino: Product = Product.Stracchino(250)
  val caciotta: Product = Product.Caciotta(500)
  val readyForQA: Batch.ReadyForQualityAssurance = Batch.ReadyForQualityAssurance(batchID, cheeseType)
}

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

  Feature("Missing stock") {
    Scenario("There are missing products from the desired stock") {
      Given("An available stock")
      val available = Map(
        squacquerone -> AvailableQuantity(10),
        casatella -> AvailableQuantity(20),
        ricotta -> AvailableQuantity(30),
        stracchino -> AvailableQuantity(40),
        caciotta -> AvailableQuantity(50),
      )
      And("a desired stock")
      val desired = Map(
        squacquerone -> DesiredQuantity(20),
        casatella -> DesiredQuantity(30),
        ricotta -> DesiredQuantity(40),
        stracchino -> DesiredQuantity(50),
        caciotta -> DesiredQuantity(60),
      )
      When("someone asks how many products are missing to reach the desired stock")
      val missingSquacquerone = getMissingCountFromProductStock(available, desired)(squacquerone)
      val missingCasatella = getMissingCountFromProductStock(available, desired)(casatella)
      val missingRicotta = getMissingCountFromProductStock(available, desired)(ricotta)
      val missingStracchino = getMissingCountFromProductStock(available, desired)(stracchino)
      val missingCaciotta = getMissingCountFromProductStock(available, desired)(caciotta)
      Then("the missing quantity should be the difference between the available and desired quantities")
      missingSquacquerone shouldBe MissingQuantity(10)
      missingCasatella shouldBe MissingQuantity(10)
      missingRicotta shouldBe MissingQuantity(10)
      missingStracchino shouldBe MissingQuantity(10)
      missingCaciotta shouldBe MissingQuantity(10)
    }
    Scenario("There are more products than needed in the desired stock") {
      Given("An available stock")
      val available = Map(squacquerone -> AvailableQuantity(20))
      And("a desired stock")
      val desired = Map(squacquerone -> DesiredQuantity(10))
      When("someone asks how many products are missing to reach the desired stock")
      val missing = getMissingCountFromProductStock(available, desired)(squacquerone)
      Then("the missing quantity should be zero")
      missing shouldBe MissingQuantity(0)
    }
    Scenario("Removal from stock with enough available products") {
      Given("An available stock")
      val available = Map(squacquerone -> AvailableQuantity(10))
      And("a quantity to remove from stock")
      val toRemove = DesiredQuantity(5)
      When("someone removes the product from the stock")
      val action: Action[NotEnoughStock, Unit, AvailableStock] = removeFromStock(available)(squacquerone, toRemove)
      Then("the stock should be updated")
      val (_, result) = action.execute
      result.value shouldEqual Map(squacquerone -> AvailableQuantity(5))
    }
    Scenario("Removal from stock with not enough available products") {
      Given("An available stock")
      val available = Map(squacquerone -> AvailableQuantity(10))
      And("a quantity to remove from stock that is greater than the available one")
      val toRemove = DesiredQuantity(50)
      When("someone removes the product from the stock")
      val action: Action[NotEnoughStock, Unit, AvailableStock] = removeFromStock(available)(squacquerone, toRemove)
      Then("an error should be raised")
      val (_, result) = action.execute
      result.left.value shouldEqual NotEnoughStock(squacquerone, toRemove, AvailableQuantity(10))
    }
  }

  Feature("Quality assurance") {
    Scenario("An operator marks a batch as passing quality assurance") {
      Given("a ready-for-QA batch")
      When("the operator passes the batch")
      val passed = approveBatch(readyForQA)
      Then("the batch should be marked as QA-passed")
      passed shouldBe a[QualityAssuredBatch.Passed]
      passed.id shouldEqual batchID
      passed.cheeseType shouldEqual cheeseType
    }
    Scenario("An operator marks a batch as failing quality assurance") {
      Given("a ready-for-QA batch")
      When("the operator fails the batch")
      val failed = rejectBatch(readyForQA)
      Then("the batch should be marked as QA-failed")
      failed shouldBe a[QualityAssuredBatch.Failed]
      failed.id shouldEqual batchID
      failed.cheeseType shouldEqual cheeseType
    }
  }

  Feature("Label printing") {
    Scenario("An operator tries to print a label for a cheese within weight range from a batch") {
      Given("a batch")
      val passed = approveBatch(readyForQA)
      And("A weight that is within the allowed range")
      val correctWeight = 102.grams
      When("the operator tries to print a label")
      val labelAction: Action[WeightNotInRange, ProductStocked, LabelledProduct] = labelProduct(passed, correctWeight)
      Then("the label should be printed with the correct information")
      val (events, result) = labelAction.execute
      val expectedLabelledProduct = LabelledProduct(squacquerone, AvailableQuantity(1), passed.id)
      result.value shouldEqual expectedLabelledProduct
      And("an event should be emitted")
      events should contain(ProductStocked(expectedLabelledProduct))
    }
    Scenario("An operator tries to print a label for a cheese outside weight range from a batch") {
      Given("a batch")
      val passed = approveBatch(readyForQA)
      And("a weight that is outside the allowed range")
      val wrongWeight = 50.grams
      When("the operator tries to print a label")
      val labelAction: Action[WeightNotInRange, ProductStocked, LabelledProduct] = labelProduct(passed, wrongWeight)
      Then("the label should not be printed")
      val (events, result) = labelAction.execute
      And("an error should be raised")
      result.left.value shouldEqual WeightNotInRange(squacquerone.weight, wrongWeight)
      And("no events should be emitted")
      events shouldBe empty
    }
  }
}
