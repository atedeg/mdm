package dev.atedeg.mdm.stocking

import cats.Monad
import cats.data.NonEmptySet

import dev.atedeg.mdm.stocking.OutgoingEvent.ProductStocked
import dev.atedeg.mdm.utils.*

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
 *
 * @note it can raise a [[WeightNotInRange weight-not-in-range]] error.
 * @note it emits a [[ProductStocked "product stocked"]] event.
 */
def labelProduct[M[_]: Monad: CanRaise[WeightNotInRange]: Emits[ProductStocked]](
    batch: QualityAssuredBatch.Passed,
    actualWeight: WeightInGrams,
): M[LabelledProduct] =
  val weights = NonEmptySet.of(WeightInGrams(1), WeightInGrams(2), WeightInGrams(3))
  val nearestWeight = getNearestWeight(weights)(actualWeight)
  val labelledProduct = LabelledProduct(batch.cheeseType, 1, batch.id)
  isWeightInRange(weights, 0.05)(actualWeight)
    .otherwiseRaise(WeightNotInRange(nearestWeight, actualWeight))
    .andThen(emit(ProductStocked(labelledProduct)))
    .thenReturn(labelledProduct)

private def getNearestWeight(expectedWeights: NonEmptySet[WeightInGrams])(actualWeight: WeightInGrams): WeightInGrams =
  expectedWeights.reduceLeft((acc, elem) =>
    if math.abs(actualWeight.grams - acc.grams) < math.abs(actualWeight.grams - elem.grams) then acc else elem,
  )

private def isWeightInRange(
    expectedWeights: NonEmptySet[WeightInGrams],
    tolerancePercentage: Double,
)(actualWeight: WeightInGrams): Boolean =
  val nearestWeight = getNearestWeight(expectedWeights)(actualWeight)
  actualWeight.grams >= nearestWeight.grams * (1 - tolerancePercentage)
  && actualWeight.grams <= nearestWeight.grams * (1 + tolerancePercentage)
