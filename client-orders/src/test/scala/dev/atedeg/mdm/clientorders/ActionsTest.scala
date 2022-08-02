package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import java.util.UUID

import cats.data.{ NonEmptyList, NonEmptyMap, Writer }
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import eu.timepit.refined.predicates.all.NonNegative
import org.scalactic.{ Explicitly, Normalization, Uniformity }
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.intToNonNegativeNumber
import dev.atedeg.mdm.utils.monads.*

extension (n: PositiveNumber)
  def of(p: Product): IncomingOrderLine = IncomingOrderLine(Quantity(n), p)
  def cents: PriceInEuroCents = PriceInEuroCents(n)

trait CustomerMock:
  private val customerId: CustomerID = CustomerID(UUID.randomUUID)
  private val customerName: CustomerName = CustomerName("Giovanni Molari")
  private val vatNumber: VATNumber = VATNumber(???)
  val customer: Customer = Customer(customerId, customerName, vatNumber)

trait LocationMock:
  private val latitude: Latitude = Latitude(-90)
  private val longitude: Longitude = Longitude(180)
  val location: Location = Location(latitude, longitude)

trait PriceListMock:

  val priceList: Product => PriceInEuroCents = Map(
    Product.Caciotta(1000) -> 100.cents,
    Product.Caciotta(500) -> 50.cents,
  )

trait OrderMocks extends CustomerMock, LocationMock:
  private val orderId: OrderID = OrderID(UUID.randomUUID)

  private val orderLines: NonEmptyList[IncomingOrderLine] = NonEmptyList.of[IncomingOrderLine](
    100 of Product.Caciotta(1000),
    100 of Product.Caciotta(500),
  )
  private val date: LocalDateTime = LocalDateTime.now
  val incomingOrder: IncomingOrder = IncomingOrder(orderId, orderLines, customer, date, location)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Tests extends AnyFeatureSpec with GivenWhenThen with Explicitly with Matchers with OrderMocks with PriceListMock:

  Feature("Order pricing") {
    Scenario("Operator prices an order") {
      Given("an incoming order")
      When("the order is priced")
      val pricedOrder = priceOrder(priceList)(incomingOrder)
      Then("the priced is computed correctly")
      val expectedPrice =
        incomingOrder.orderLines.map(ol => priceList(ol.product).n * ol.quantity.n).reduce(_ + _).cents
      pricedOrder.totalPrice shouldBe expectedPrice
    }
  }

  Feature("Order preparation") {
    Scenario("An order is prepared") {
      Given("a priced order")
      val pricedOrder = priceOrder(priceList)(incomingOrder)
      When("the order is marked as in progress")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      Then("it should not contain any palletized product")
      val nonEmptyLines = inProgressOrder.orderLines.filter(ol =>
        ol match
          case _: InProgressOrderLine.Complete => false
          case ol: InProgressOrderLine.Incomplete => ol.actual.n === 0,
      )
      nonEmptyLines shouldBe empty
    }

    Scenario("A product is palletized for an order that does not require it") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product that is not requested by the order")
      val productNotInOrder = Ricotta(350)
      When("the operator tries to palletize it")
      val palletizeAction = palletizeProductForOrder(inProgressOrder)(Quantity(10), productNotInOrder)
      Then("a ProductNotInOrder error should be raised")
      // val result = palletizeAction.execute
      ???
    }

    Scenario("A product is palletized in a quantity greater than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator tries to palletize it in a quantity greater than the required one")
      val palletizeAction = palletizeProductForOrder(inProgressOrder)(Quantity(1000), productInOrder)
      Then("a PalletizedMoreThanRequired error should be raised")
      // val result = palletizeAction.execute
      ???
    }

    Scenario("A product is palletized in the exact quantity") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in the exact required quantity")
      val palletizeAction = palletizeProductForOrder(inProgressOrder)(Quantity(100), productInOrder)
      Then("the corresponding order line is marked as completed")
      ???
    }

    Scenario("A product is palletized in a quantity lower than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in a quantity lower than the required one")
      val palletizeAction = palletizeProductForOrder(inProgressOrder)(Quantity(20), productInOrder)
      Then("the corresponding order line is updated")
      ???
    }
  }

  Feature("Order completion") {
    Scenario("An incomplete order is completed") {
      Given("an incomplete in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      When("one tries to mark it as completed")
      val completeAction = completeOrder(inProgressOrder)
      Then("an OrderCompletionError is raised")
      ???
    }

    Scenario("A complete order is completed") {
      Given("a complete in-progress order")
      When("one marks it as completed")
      Then("it is completed correctly")
    }
  }

  Feature("Order transportation") {
    Scenario("The order weight is computed") {
      Given("an order")
      When("the weight is computed")
      Then("it is the exact sum of the weights of its products")
    }

    Scenario("A transport document is printed") {
      Given("an order")
      When("one requests the transport document")
      Then("the correct transport document is generated")
    }
  }
