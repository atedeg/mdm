package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime
import java.util.UUID

import OrderCompletionError.*
import PalletizationError.*
import cats.Monad
import cats.data.{ NonEmptyList, NonEmptyMap, Writer }
import cats.implicits.catsKernelOrderingForOrder
import cats.syntax.all.*
import eu.timepit.refined.predicates.all.NonNegative
import eu.timepit.refined.string.MatchesRegex
import org.scalactic.{ Explicitly, Normalization, Uniformity }
import org.scalatest.EitherValues.*
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import dev.atedeg.mdm.clientorders.utils.*
import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.intToNonNegativeNumber
import dev.atedeg.mdm.utils.monads.*

trait CustomerMock:
  private val customerId: CustomerID = CustomerID(UUID.randomUUID)
  private val customerName: CustomerName = CustomerName("Giovanni Molari")
  private val vatNumber: VATNumber = VATNumber(coerce("IT12345678901"))
  val customer: Customer = Customer(customerId, customerName, vatNumber)

trait LocationMock:
  private val latitude: Latitude = Latitude(-90)
  private val longitude: Longitude = Longitude(180)
  val location: Location = Location(latitude, longitude)

trait PriceListMock:

  val priceList: Product => PriceInEuroCents = Map(
    Caciotta(1000) -> 100.euroCents,
    Caciotta(500) -> 50.euroCents,
  )

trait OrderMocks extends PriceListMock, CustomerMock, LocationMock:
  private val orderId: OrderID = OrderID(UUID.randomUUID)

  private val orderLines: NonEmptyList[IncomingOrderLine] = NonEmptyList.of[IncomingOrderLine](
    100 of Caciotta(1000),
    100 of Caciotta(500),
  )
  private val date: LocalDateTime = LocalDateTime.now
  val incomingOrder: IncomingOrder = IncomingOrder(orderId, orderLines, customer, date, location)

  val inProgressCompleteOrder: InProgressOrder =
    def palletizeAll[M[_]: Monad: CanRaise[PalletizationError]](inProgressOrder: InProgressOrder): M[InProgressOrder] =
      palletizeProductForOrder(Quantity(100), Caciotta(500))(inProgressOrder)
        >>= palletizeProductForOrder(Quantity(100), Caciotta(1000))

    val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
    val palletizeAction: Action[PalletizationError, Unit, InProgressOrder] = palletizeAll(inProgressOrder)
    palletizeAction.execute._2.value

  val completedOrder: CompletedOrder =
    val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(inProgressCompleteOrder)
    completeAction.execute._2.value

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Tests extends AnyFeatureSpec with GivenWhenThen with Explicitly with Matchers with OrderMocks:

  Feature("Order pricing") {
    Scenario("Operator prices an order") {
      Given("an incoming order")
      When("the order is priced")
      val pricedOrder = priceOrder(priceList)(incomingOrder)
      Then("the priced is computed correctly")
      val expectedPrice =
        incomingOrder.orderLines.map(ol => priceList(ol.product).n * ol.quantity.n).reduce(_ + _).euroCents
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
      inProgressOrder.orderLines shouldBe NonEmptyList.of(
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }

    Scenario("A product is palletized for an order that does not require it") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product that is not requested by the order")
      val productNotInOrder = Ricotta(350)
      When("the operator tries to palletize it")
      val palletizeAction: Action[PalletizationError, Unit, InProgressOrder] =
        palletizeProductForOrder(Quantity(10), productNotInOrder)(inProgressOrder)
      Then("a ProductNotInOrder error should be raised")
      val (_, result) = palletizeAction.execute
      result.left.value shouldBe ProductNotInOrder(productNotInOrder)
    }

    Scenario("A product is palletized in a quantity greater than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator tries to palletize it in a quantity greater than the required one")
      val palletizeAction: Action[PalletizationError, Unit, InProgressOrder] =
        palletizeProductForOrder(Quantity(1000), productInOrder)(inProgressOrder)
      Then("a PalletizedMoreThanRequired error should be raised")
      val (_, result) = palletizeAction.execute
      result.left.value shouldBe PalletizedMoreThanRequired(MissingQuantity(100))
    }

    Scenario("A product is palletized in the exact quantity") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in the exact required quantity")
      val palletizeAction: Action[PalletizationError, Unit, InProgressOrder] =
        palletizeProductForOrder(Quantity(100), productInOrder)(inProgressOrder)
      Then("the corresponding order line is marked as completed")
      val (_, result) = palletizeAction.execute
      result.value.orderLines shouldBe NonEmptyList.of(
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Complete(Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }

    Scenario("A product is palletized in a quantity lower than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in a quantity lower than the required one")
      val palletizeAction: Action[PalletizationError, Unit, InProgressOrder] =
        palletizeProductForOrder(Quantity(20), productInOrder)(inProgressOrder)
      Then("the corresponding order line is updated")
      val (_, result) = palletizeAction.execute
      result.value.orderLines shouldBe NonEmptyList.of(
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Incomplete(PalletizedQuantity(20), Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }
  }

  Feature("Order completion") {
    Scenario("An incomplete order is completed") {
      Given("an incomplete in-progress order")
      val inProgressOrder = startPreparingOrder(priceOrder(priceList)(incomingOrder))
      When("one tries to mark it as completed")
      val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(inProgressOrder)
      Then("an OrderCompletionError is raised")
      val (_, result) = completeAction.execute
      result.left.value shouldBe OrderNotComplete()
    }

    Scenario("A complete order is completed") {
      Given("a complete in-progress order")
      val order = inProgressCompleteOrder
      When("one marks it as completed")
      val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(order)
      Then("it is completed correctly")
      val (_, completed) = completeAction.execute
      completed.value shouldBe CompletedOrder(
        order.id,
        NonEmptyList.of(
          CompleteOrderLine(Quantity(100), Caciotta(1000), 10_000.euroCents),
          CompleteOrderLine(Quantity(100), Caciotta(500), 5000.euroCents),
        ),
        order.customer,
        order.deliveryDate,
        order.deliveryLocation,
        order.totalPrice,
      )
    }
  }

  Feature("Order transportation") {
    Scenario("The order weight is computed") {
      Given("an order")
      val order = completedOrder
      When("the weight is computed")
      val weight = weightOrder(order)
      Then("it is the exact sum of the weights of its products")
      val expectedGrams = order.orderLines.map(ol => ol.quantity.n * ol.product.weight.n).reduce(_ + _)
      val expectedKilograms = WeightInKilograms(expectedGrams.toDecimal / 1000)
      weight shouldBe expectedKilograms
    }

    Scenario("A transport document is created") {
      Given("an order")
      val order = completedOrder
      val weight = weightOrder(order)
      When("one requests the transport document")
      val td = createTransportDocument(order, weight)
      Then("the correct transport document is generated")
      td.customer shouldBe order.customer
      td.deliveryLocation shouldBe order.deliveryLocation
      td.totalWeight shouldBe weight
      td.transportDocumentLines shouldBe NonEmptyList.of(
        TransportDocumentLine(Quantity(100), Caciotta(1000)),
        TransportDocumentLine(Quantity(100), Caciotta(500)),
      )
    }
  }
