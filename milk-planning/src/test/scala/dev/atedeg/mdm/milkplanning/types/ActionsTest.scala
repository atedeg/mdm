package dev.atedeg.mdm.milkplanning.types

import cats.data.Writer
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.milkplanning.dsl.*
import dev.atedeg.mdm.milkplanning.types.OutgoingEvent.OrderMilk

class ActionsTest extends AnyFeatureSpec with GivenWhenThen with Matchers {

  Feature("Estimate the quintals of milk needed for the following week") {
//    Scenario("Simple estimation") {
//      Given("A request for ordering milk")
//      val qomPreviousYear = 4.0.quintalsOfMilk
//      val qomNeededByProd = 3.5.quintalsOfMilk
//      val currentStock: Stock = _ => 0.stockedQuantity
//      val stockedMilk = 0.0.quintalsOfMilk
//      When("the estimation is completed")
//      val estimationMonad: Writer[List[OrderMilk], QuintalsOfMilk] = estimateQuintalsOfMilk(
//        qomPreviousYear,
//        qomNeededByProd,
//        currentStock,
//        stockedMilk,
//      )
//      Then("an event should be raised in order to place the order")
//      val (events, qom) = estimationMonad.run
//      events should have length 1
//      events.headOption match {
//        case Some(e) => e.n should equal(qom)
//        case _ => fail("The events list must have at least one element")
//      }
//    }
  }
}
