package dev.atedeg.mdm.stocking

import cats.Monad
import cats.data.{ NonEmptyList, NonEmptySet }
import cats.implicits.toReducibleOps

import dev.atedeg.mdm.stocking.OutgoingEvent.*
import dev.atedeg.mdm.stocking.grams
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
 */
def labelProduct[M[_]: Monad: CanRaise[WeightNotInRange]: CanEmit[ProductStocked]](
    batch: QualityAssuredBatch.Passed,
    actualWeight: WeightInGrams,
): M[LabelledProduct] =
  val weights = NonEmptyList.of(1.grams, 2.grams, 3.grams) // FIXME: get available weights
  val candidate = nearestWeight(weights)(actualWeight)
  val labelledProduct = LabelledProduct(batch.cheeseType, 1, batch.id)
  actualWeight.grams
    .isInRange(candidate.grams +- 5.percent)
    .otherwiseRaise(WeightNotInRange(candidate, actualWeight))
    .andThen(emit(ProductStocked(labelledProduct): ProductStocked))
    .thenReturn(labelledProduct)

private def nearestWeight(weights: NonEmptyList[WeightInGrams])(actualWeight: WeightInGrams): WeightInGrams =
  def distanceFromActualWeight(weight: WeightInGrams) = math.abs(weight.grams - actualWeight.grams)
  weights.minimumBy(distanceFromActualWeight)
