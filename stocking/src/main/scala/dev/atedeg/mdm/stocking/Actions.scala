package dev.atedeg.mdm.stocking

import cats.Monad
import cats.data.{ NonEmptyList, NonEmptySet }
import cats.implicits.toReducibleOps
import eu.timepit.refined.auto.autoUnwrap

import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.stocking.Errors.*
import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.stocking.grams
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Gets how many products are missing from the stock, given the desired stock.
 */
def getMissingCountFromProductStock(
    availableStock: AvailableStock,
    desiredStock: DesiredStock,
)(product: Product): AvailableQuantity = AvailableQuantity(
  availableStock(product).n - desiredStock(product).n.toNonNegative,
)

/**
 * Removes the given quantity of a certain product from the stock, giving the new current stock.
 */
def removeFromStock[M[_]: Monad: CanRaise[NotEnoughStock]](
    stock: AvailableStock,
)(product: Product, quantity: AvailableQuantity): M[AvailableStock] =
  (stock(product).n >= quantity.n)
    .otherwiseRaise(NotEnoughStock(product, quantity, stock(product)): NotEnoughStock)
    .thenReturn(stock + (product -> AvailableQuantity(stock(product).n - quantity.n)))

/**
 * Approves a batch after quality assurance.
 */
def approveBatch(batch: Batch.ReadyForQualityAssurance): QualityAssuredBatch.Passed =
  QualityAssuredBatch.Passed(batch.id, batch.cheeseType)

/**
 * Rejects a batch after quality assurance.
 */
def rejectBatch(batch: Batch.ReadyForQualityAssurance): QualityAssuredBatch.Failed =
  QualityAssuredBatch.Failed(batch.id, batch.cheeseType)

/**
 * Labels a product given the [[QualityAssuredBatch.Passed batch]] it comes from and its [[WeightInGrams actual weight]]
 * as given by the scale.
 */
def labelProduct[M[_]: Monad: CanRaise[WeightNotInRange]: CanEmit[ProductStocked]](
    batch: QualityAssuredBatch.Passed,
    actualWeight: Grams[PositiveNumber],
): M[LabelledProduct] =
  batch.cheeseType match
    case CheeseType.Squacquerone =>
      val candidate = nearestWeight(allSquacqueroneWeights)(actualWeight)
      ???
    case _ => ???

private def nearestWeight(weights: NonEmptyList[Grams[Int]])(
    actualWeight: Grams[Int],
): Grams[Int] =
  def distanceFromActualWeight(weight: Grams[Int]) = math.abs(weight.n - actualWeight.n)
  weights.minimumBy(distanceFromActualWeight)
