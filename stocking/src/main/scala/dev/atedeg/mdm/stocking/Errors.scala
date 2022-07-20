package dev.atedeg.mdm.stocking

/**
 * An error raised by the [[labelProduct() labelling action]] if the actual weight is too far
 * from the expected weights.
 */
final case class WeightNotInRange(expectedWeight: WeightInGrams, actualWeight: WeightInGrams)
