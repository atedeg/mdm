package dev.atedeg.mdm.production

import java.util.UUID

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

trait Mocks {}

class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks {

  Feature("Production management") {
    Scenario("A production is started") {
      Given("a production that has to be started")
      When("it is started")
      Then("an event is emitted to notify that the production should start")
      And("the correct amount of products is computed")
      And("the production is started")
    }

    Scenario("A production is started with no recipe") {
      Given("a production that has to be started")
      And("has no recipe")
      When("it is started")
      Then("an error is raised")
      And("no production is started")
    }

    Scenario("A production is ended") {
      Given("a production that is in progress")
      When("it is ended")
      Then("it should assign it a correct lot number")
      And("emit an event to notify that the production ended")
    }
  }
}
