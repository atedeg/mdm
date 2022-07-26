package dev.atedeg.mdm.stocking

import cats.Monad
import cats.data.{ NonEmptyList, NonEmptySet }

import dev.atedeg.mdm.stocking.OutgoingEvent.ProductStocked
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.monads.*

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
def labelProduct[M[_]: Monad: CanRaise[WeightNotInRange]: CanEmit[ProductStocked]](
    batch: QualityAssuredBatch.Passed,
    actualWeight: WeightInGrams,
): M[LabelledProduct] =
  val weights = NonEmptyList.of(WeightInGrams(1), WeightInGrams(2), WeightInGrams(3))
  val candidate = nearestWeight(weights)(actualWeight)
  val labelledProduct = LabelledProduct(batch.cheeseType, 1, batch.id)
  actualWeight.grams
    .isInRange(candidate.grams +- 5.percent)
    .otherwiseRaise(WeightNotInRange(candidate, actualWeight))
    .andThen(emit(ProductStocked(labelledProduct): ProductStocked))
    .thenReturn(labelledProduct)

private def nearestWeight(expectedWeights: NonEmptyList[WeightInGrams])(actualWeight: WeightInGrams): WeightInGrams =
  expectedWeights.reduceLeft((acc, elem) =>
    if math.abs(actualWeight.grams - elem.grams) < math.abs(actualWeight.grams - acc.grams) then elem else acc,
  )
