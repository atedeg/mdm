package dev.atedeg.mdm.productionplanning

import java.time.LocalDate

import cats.Monad
import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.productionplanning.{ CheeseTypeRipeningDays, RipeningDays }
import dev.atedeg.mdm.productionplanning.OutgoingEvent.{ OrderDelayed, ProductionPlanReady }
import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.products.CheeseType
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Estimate how many [[Product products]] to produce that day.
 * To do so, takes into account the [[ProductionPlan production plan]] for the same day of the previous year,
 * the new [[Order orders]] considering the [[RipeningDays ripening days]] of each [[CheeseType cheese type]] and the
 * [[Product products]] needed to replenish the stock.
 * If an order cannot be fulfilled since it contains products whose ripening days make it impossible
 * to satisfy the order by the required date, it emits an [[OrderDelayed order delayed]] event.
 */
def createProductionPlan[M[_]: Monad: Emits[ProductionPlanReady]: CanEmit[OrderDelayed]](
    stock: Stock,
    cheeseTypeRipeningDays: CheeseTypeRipeningDays,
)(
    previousProductionPlan: ProductionPlan,
    orders: List[Order],
): M[ProductionPlan] = for {
  _ <- orders.traverse(checkDeliverabilityOfOrder(cheeseTypeRipeningDays))
  productsToProduce = magicAIProductsToProduceEstimator(orders, previousProductionPlan, cheeseTypeRipeningDays, stock)
  productionPlan = ProductionPlan(productsToProduce)
  _ <- emit(ProductionPlanReady(productionPlan): ProductionPlanReady)
} yield productionPlan

private def checkDeliverabilityOfOrder[M[_]: Monad: CanEmit[OrderDelayed]](
    cheeseTypeRipeningDays: CheeseTypeRipeningDays,
)(order: Order): M[Unit] = {
  val ripeningDays = order.orderedProducts.map(_.product.cheeseType).map(cheeseTypeRipeningDays(_))
  val isDelayed = ripeningDays.map(productionInTime(_, order.requiredBy)).exists(_ == OrderStatus.Delayed)
  when(isDelayed) {
    val deliveryDate = newDeliveryDate(RipeningDays(ripeningDays.map(_.days).reduceLeft(max)))
    emit(OrderDelayed(order.orderdID, deliveryDate): OrderDelayed)
  }
}

private def magicAIProductsToProduceEstimator(
    orders: List[Order],
    previousProductionPlan: ProductionPlan,
    cheeseTypeRipeningDays: CheeseTypeRipeningDays,
    stock: Stock,
): NonEmptyList[ProductToProduce] =
  // This is a mock, ideally that would be estimated by an intelligent agent or some heuristics.
  NonEmptyList.of(ProductToProduce(Product.Caciotta(500), Quantity(5)))

private def productionInTime(ripeningDays: RipeningDays, requiredBy: LocalDate): OrderStatus =
  val today = LocalDate.now
  if today.plusDays(ripeningDays.days.value.toLong).isBefore(requiredBy)
  then OrderStatus.NonDelayed
  else OrderStatus.Delayed

private def newDeliveryDate(ripeningDays: RipeningDays): LocalDate =
  val today = LocalDate.now
  today.plusDays(ripeningDays.days.value.toLong)

private enum OrderStatus:
  case Delayed
  case NonDelayed
