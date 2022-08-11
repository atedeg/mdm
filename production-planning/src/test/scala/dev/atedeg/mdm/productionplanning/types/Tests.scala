package dev.atedeg.mdm.productionplanning.types

import java.time.LocalDate
import java.util.UUID

import cats.data.{ NonEmptyList, Writer }
import cats.syntax.all.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.productionplanning.*
import dev.atedeg.mdm.productionplanning.OutgoingEvent.{ OrderDelayed, ProductionPlanReady }
import dev.atedeg.mdm.products.{ CheeseType, Product }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

trait Mocks:

  val cheeseTypeRipeningDays: CheeseTypeRipeningDays = CheeseTypeRipeningDays(
    Map(
      CheeseType.Squacquerone -> RipeningDays(4),
      CheeseType.Ricotta -> RipeningDays(0),
      CheeseType.Caciotta -> RipeningDays(8),
      CheeseType.Casatella -> RipeningDays(4),
      CheeseType.Stracchino -> RipeningDays(5),
    ),
  )

  val prodToProd1: ProductToProduce = ProductToProduce(Product.Caciotta(500), Quantity(5))
  val prodToProd2: ProductToProduce = ProductToProduce(Product.Casatella(300), Quantity(10))
  val prodToProd3: ProductToProduce = ProductToProduce(Product.Squacquerone(250), Quantity(10))

  val orderedProd1: OrderedProduct = OrderedProduct(Product.Caciotta(500), Quantity(5))
  val orderedProd2: OrderedProduct = OrderedProduct(Product.Casatella(300), Quantity(10))
  val orderedProd3: OrderedProduct = OrderedProduct(Product.Squacquerone(250), Quantity(10))

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Tests extends AnyFeatureSpec with GivenWhenThen with Matchers with Mocks:

  Feature("Create the production plan for the day") {
    Scenario("Raffaella wants to create the production plan") {
      Given("a list of orders with deadline in 10 days")
      val requiredBy = LocalDate.now.plusDays(10)
      val orderedProducts = NonEmptyList.of(orderedProd1, orderedProd2, orderedProd3)
      val orders = List.fill(3)(Order(OrderID(UUID.randomUUID), requiredBy, orderedProducts))
      And("the production plan of the previous year for the same day")
      val productsToProduce = NonEmptyList.of(prodToProd1, prodToProd2, prodToProd3)
      val previousProductionPlan = ProductionPlan(productsToProduce)
      And("an empty stock")
      val stock: Stock = Stock(Map.empty)
      When("creating the production plan")
      val productionPlanCreation: SafeActionTwoEvents[ProductionPlanReady, OrderDelayed, ProductionPlan] =
        createProductionPlan(stock, cheeseTypeRipeningDays)(previousProductionPlan, orders)
      val (events1, events2, productionPlan) = productionPlanCreation.execute
      Then("the result is the same production plan emitted in the event")
      events1 should not be empty
      events1.map(_.productionPlan) should contain(productionPlan)
      events2 shouldBe empty

      Given("a list of orders with deadline in 6 days (which is less than the ordered Caciotta's ripening time)")
      val requiredBy2 = LocalDate.now.plusDays(6)
      val orderedProducts1 = NonEmptyList.of(orderedProd1, orderedProd2, orderedProd3)
      val orderedProducts2 = NonEmptyList.of(orderedProd2, orderedProd3)
      val orderedProducts3 = NonEmptyList.of(orderedProd2, orderedProd3)
      val order1ID = OrderID(UUID.randomUUID())
      val order2ID = OrderID(UUID.randomUUID())
      val order3ID = OrderID(UUID.randomUUID())
      val orders2 = List(
        Order(order1ID, requiredBy2, orderedProducts1),
        Order(order2ID, requiredBy2, orderedProducts2),
        Order(order3ID, requiredBy2, orderedProducts3),
      )
      And("the production plan of the previous year for the same day")
      And("an empty stock")
      When("creating the production plan")
      val productionPlanCreation2: SafeActionTwoEvents[ProductionPlanReady, OrderDelayed, ProductionPlan] =
        createProductionPlan(stock, cheeseTypeRipeningDays)(previousProductionPlan, orders2)
      val (e1, e2, pp) = productionPlanCreation2.execute
      Then("Should delay the order containing the Cacciotta product")
      e1 should not be empty
      e1.map(_.productionPlan) should contain(pp)
      val newDeliveryDate = LocalDate.now.plusDays(8)
      e2 should contain(OrderDelayed(order1ID, newDeliveryDate))
    }
  }
