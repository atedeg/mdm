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

import dev.atedeg.mdm.clientorders.OutgoingEvent.{ OrderProcessed, ProductPalletized }
import dev.atedeg.mdm.clientorders.dto.ProductPalletizedDTO
import dev.atedeg.mdm.clientorders.utils.*
import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.Product.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

trait ClientMock:
  private val clientId: ClientID = ClientID(UUID.randomUUID)
  private val clientName: ClientName = ClientName("Giovanni Molari")
  private val vatNumber: VATNumber = VATNumber(coerce("IT12345678901"))
  val client: Client = Client(clientId, clientName, vatNumber)

trait LocationMock:
  private val latitude: Latitude = Latitude(-90)
  private val longitude: Longitude = Longitude(180)
  val location: Location = Location(latitude, longitude)

trait OrderMocks extends ClientMock, LocationMock:
  private val orderId: OrderID = OrderID(UUID.randomUUID)

  private val orderLines: NonEmptyList[IncomingOrderLine] = NonEmptyList.of(
    100 of Caciotta(1000),
    100 of Caciotta(500),
  )
  val orderLinesPrices: NonEmptyList[PriceInEuroCents] = NonEmptyList.of(
    PriceInEuroCents(10000),
    PriceInEuroCents(5000),
  )
  private val date: LocalDateTime = LocalDateTime.now
  val incomingOrder: IncomingOrder = IncomingOrder(orderId, orderLines, client, date, location)

  val inProgressCompleteOrder: InProgressOrder =
    def palletizeAll[M[_]: Monad: CanRaise[PalletizationError]: Emits[ProductPalletized]](
        inProgressOrder: InProgressOrder,
    ): M[InProgressOrder] =
      palletizeProductForOrder(Quantity(100), Caciotta(500))(inProgressOrder)
        >>= palletizeProductForOrder(Quantity(100), Caciotta(1000))

    val pricedOrderLines = orderLines.zip(orderLinesPrices).map(priceOrderLine)
    val pricedOrderAction: SafeAction[OrderProcessed, PricedOrder] = priceOrder(incomingOrder, pricedOrderLines)
    val (_, pricedOrder) = pricedOrderAction.execute
    val inProgressOrder = startPreparingOrder(pricedOrder)
    val palletizeAction: Action[PalletizationError, ProductPalletized, InProgressOrder] = palletizeAll(inProgressOrder)
    palletizeAction.execute._2.value

  val pricedOrder: PricedOrder =
    val pricedOrderLines = orderLines.zip(orderLinesPrices).map(priceOrderLine)
    val pricedOrderAction: SafeAction[OrderProcessed, PricedOrder] = priceOrder(incomingOrder, pricedOrderLines)
    pricedOrderAction.execute._2

  val completedOrder: CompletedOrder =
    val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(inProgressCompleteOrder)
    completeAction.execute._2.value

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Tests extends AnyFeatureSpec with GivenWhenThen with Explicitly with Matchers with OrderMocks:

  Feature("Order pricing") {
    Scenario("Operator prices an order") {
      Given("an incoming order")
      val order = incomingOrder
      And("the prices for each incoming order line")
      val prices = orderLinesPrices
      When("the order is processed")
      val pricedOrderLines = order.orderLines.zip(prices).map(priceOrderLine)
      val priceAction: SafeAction[OrderProcessed, PricedOrder] = priceOrder(incomingOrder, pricedOrderLines)
      val (events, pricedOrder) = priceAction.execute
      Then("the price is computed correctly")
      pricedOrder.totalPrice shouldBe PriceInEuroCents(10000 + 5000)
      And("an event is emitted")
      events shouldBe List(OrderProcessed(incomingOrder))
    }
  }

  Feature("Order preparation") {
    Scenario("An order is prepared") {
      Given("a priced order")
      When("the order is marked as in progress")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      Then("it should not contain any palletized product")
      inProgressOrder.orderLines.toList should contain allOf (
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }

    Scenario("A product is palletized for an order that does not require it") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      And("a product that is not requested by the order")
      val productNotInOrder = Ricotta(350)
      When("the operator tries to palletize it")
      val palletizeAction: Action[PalletizationError, ProductPalletized, InProgressOrder] =
        palletizeProductForOrder(Quantity(10), productNotInOrder)(inProgressOrder)
      Then("a ProductNotInOrder error should be raised")
      val (events, result) = palletizeAction.execute
      events shouldBe empty
      result.left.value shouldBe ProductNotInOrder(productNotInOrder)
    }

    Scenario("A product is palletized in a quantity greater than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator tries to palletize it in a quantity greater than the required one")
      val palletizeAction: Action[PalletizationError, ProductPalletized, InProgressOrder] =
        palletizeProductForOrder(Quantity(1000), productInOrder)(inProgressOrder)
      Then("a PalletizedMoreThanRequired error should be raised")
      val (events, result) = palletizeAction.execute
      events shouldBe empty
      result.left.value shouldBe PalletizedMoreThanRequired(MissingQuantity(100))
    }

    Scenario("A product is palletized in the exact quantity") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in the exact required quantity")
      val palletizeAction: Action[PalletizationError, ProductPalletized, InProgressOrder] =
        palletizeProductForOrder(Quantity(100), productInOrder)(inProgressOrder)
      Then("the corresponding order line is marked as completed")
      val (events, result) = palletizeAction.execute
      events should contain(ProductPalletized(productInOrder, Quantity(100)))
      result.value.orderLines.toList should contain allOf (
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Complete(Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }

    Scenario("A product is palletized in a quantity lower than the required one") {
      Given("an in-progress order")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      And("a product requested by the order")
      val productInOrder = Caciotta(500)
      When("the operator palletizes it in a quantity lower than the required one")
      val palletizeAction: Action[PalletizationError, ProductPalletized, InProgressOrder] =
        palletizeProductForOrder(Quantity(20), productInOrder)(inProgressOrder)
      Then("the corresponding order line is updated")
      val (events, result) = palletizeAction.execute
      events should contain(ProductPalletized(productInOrder, Quantity(20)))
      result.value.orderLines.toList should contain allOf (
        InProgressOrderLine.Incomplete(PalletizedQuantity(0), Quantity(100), Caciotta(1000), 10_000.euroCents),
        InProgressOrderLine.Incomplete(PalletizedQuantity(20), Quantity(100), Caciotta(500), 5000.euroCents),
      )
    }
  }

  Feature("Order completion") {
    Scenario("An incomplete order is completed") {
      Given("an incomplete in-progress order")
      val inProgressOrder = startPreparingOrder(pricedOrder)
      When("one tries to mark it as completed")
      val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(inProgressOrder)
      Then("an OrderCompletionError is raised")
      val (_, result) = completeAction.execute
      result.left.value shouldBe OrderNotComplete()
    }

    Scenario("A complete order is completed") {
      Given("a complete in-progress order")
      When("one marks it as completed")
      val completeAction: Action[OrderCompletionError, Unit, CompletedOrder] = completeOrder(inProgressCompleteOrder)
      Then("it is completed correctly")
      val (_, completed) = completeAction.execute
      completed.value shouldBe CompletedOrder(
        inProgressCompleteOrder.id,
        NonEmptyList.of(
          CompleteOrderLine(Quantity(100), Caciotta(1000), 10_000.euroCents),
          CompleteOrderLine(Quantity(100), Caciotta(500), 5000.euroCents),
        ),
        inProgressCompleteOrder.client,
        inProgressCompleteOrder.deliveryDate,
        inProgressCompleteOrder.deliveryLocation,
        inProgressCompleteOrder.totalPrice,
      )
    }
  }

  Feature("Order transportation") {
    Scenario("The order weight is computed") {
      Given("a completed order")
      When("the weight is computed")
      val weight = weightOrder(completedOrder)
      Then("it is the exact sum of the weights of its products")
      val expectedGrams = completedOrder.orderLines.map(ol => ol.quantity.n * ol.product.weight.n).reduce(_ + _)
      val expectedKilograms = WeightInKilograms(expectedGrams.toDecimal / 1000)
      weight shouldBe expectedKilograms
    }

    Scenario("A transport document is created") {
      Given("a completed order")
      val weight = weightOrder(completedOrder)
      When("one requests the transport document")
      val td = createTransportDocument(completedOrder, weight)
      Then("the correct transport document is generated")
      td.client shouldBe completedOrder.client
      td.deliveryLocation shouldBe completedOrder.deliveryLocation
      td.totalWeight shouldBe weight
      td.transportDocumentLines.toList should contain allOf (
        TransportDocumentLine(Quantity(100), Caciotta(1000)),
        TransportDocumentLine(Quantity(100), Caciotta(500)),
      )
    }
  }
