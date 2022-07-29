package dev.atedeg.mdm.stocking

import java.util.UUID

import cats.data.{ Writer, WriterT }
import org.scalatest.EitherValues.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.stocking.grams
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.monads.*

trait Mocks {
  val batchID: BatchID = BatchID(UUID.randomUUID())
  val cheeseType: CheeseType = 0
  val readyForQA: Batch.ReadyForQualityAssurance = Batch.ReadyForQualityAssurance(batchID, cheeseType)
}

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

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
    Scenario("An operator tries to print a label for a cheese within weight range from a batch") {
      Given("a batch")
      val passed = approveBatch(readyForQA)
      val weight = 1.grams
      When("the operator prints a label for a product within weight range")
      val labelAction: Action[WeightNotInRange, ProductStocked, LabelledProduct] = labelProduct(passed, weight)
      Then("the label should be printed with the correct information")
      val (events, result) = labelAction.execute
      val expectedLabelledProduct = LabelledProduct(passed.cheeseType, 1, passed.id)
      result.value shouldEqual expectedLabelledProduct
      And("an event should be emitted")
      events should contain(ProductStocked(expectedLabelledProduct))
    }
    Scenario("An operator tries to print a label for a cheese outside weight range from a batch") {
      Given("a batch")
      val passed = approveBatch(readyForQA)
      val weight = 100.grams
      When("the operator prints a label for a cheese outside weight range")
      val labelAction: Action[WeightNotInRange, ProductStocked, LabelledProduct] = labelProduct(passed, weight)
      Then("the label should not be printed")
      val (events, result) = labelAction.execute
      And("an error should be raised")
      result.left.value shouldEqual WeightNotInRange(3.grams, 100.grams)
      And("no events should be emitted")
      events shouldBe empty
    }
  }
}