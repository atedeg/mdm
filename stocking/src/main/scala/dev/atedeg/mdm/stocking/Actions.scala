package dev.atedeg.mdm.stocking

import cats.Monad
import cats.data.{ NonEmptyList, NonEmptySet }
import cats.syntax.all.*

import dev.atedeg.mdm.products.*
import dev.atedeg.mdm.products.given
import dev.atedeg.mdm.stocking.Errors.*
import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import dev.atedeg.mdm.utils.monads.*

/**
 * Gets how many products are missing from the stock, given the desired stock.
 */
def getMissingCountFromProductStock(
    availableStock: AvailableStock,
    desiredStock: DesiredStock,
)(product: Product): MissingQuantity =
  if availableStock(product).n >= desiredStock(product).n.toNonNegative then MissingQuantity(0)
  else MissingQuantity(desiredStock(product).n.toNonNegative - availableStock(product).n)

/**
 * Removes the given quantity of a certain product from the stock, giving the new current stock.
 */
def removeFromStock[M[_]: Monad: CanRaise[NotEnoughStock]](
    stock: AvailableStock,
)(product: Product, quantity: DesiredQuantity): M[AvailableStock] =
  (stock(product).n > quantity.n.toNonNegative)
    .otherwiseRaise(NotEnoughStock(product, quantity, stock(product)): NotEnoughStock)
    .thenReturn(stock + (product -> AvailableQuantity(stock(product).n - quantity.n.toNonNegative)))

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
    actualWeight: Grams,
): M[LabelledProduct] =
  val closestAllowedWeight = batch.cheeseType.allowedWeights.closestTo(actualWeight)
  for {
    product <- batch.cheeseType
      .withWeight(closeTo(actualWeight))
      .ifMissingRaise(WeightNotInRange(closestAllowedWeight, actualWeight): WeightNotInRange)
    labelledProduct = LabelledProduct(product, AvailableQuantity(1), batch.id)
    _ <- emit(ProductStocked(labelledProduct): ProductStocked)
  } yield labelledProduct

private def closeTo(weight: Grams)(n: Int): Boolean = weight.n.value.toDouble isInRange (n.toDouble +- 5.percent)

extension (gs: NonEmptyList[Grams]) private def closestTo(g: Grams): Grams = gs.minimumBy(_.n.value - g.n.value)
