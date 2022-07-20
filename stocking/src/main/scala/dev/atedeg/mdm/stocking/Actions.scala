package dev.atedeg.mdm.stocking

/**
 * Approves a batch after quality assurance.
 */
def approveBatch(batch: Batch.ReadyForQualityAssurance): QualityAssuredBatch.Passed = ???

/**
 * Rejects a batch after quality assurance.
 */
def rejectBatch(batch: Batch.ReadyForQualityAssurance): QualityAssuredBatch.Failed = ???

/**
 * Labels a product given the [[Batch batch]] it comes from and its [[WeightInGrams actual weight]]
 * as given by the scale.
 *
 * @note it can raise a [[WeightNotInRange weight-not-in-range]] error.
 * @note it emits a [[StockedProduct "stocked product"]] event.
 */
// TODO: can fail (weight not in range), can emit StockedProduct
def labelProduct(batch: Batch, actualWeight: WeightInGrams): LabelledProduct = ???
