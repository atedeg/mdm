package dev.atedeg.mdm.stocking

import dev.atedeg.mdm.products.Product

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * Fired when a label is printed for a [[Product product]], which is then stocked.
   */
  case ProductStocked(labelledProduct: LabelledProduct)

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:
  /**
   * Received when a [[Batch batch]] is ready for quality assurance.
   */
  case BatchReadyForQualityAssurance(batch: BatchID)
  case ProductRemovedFromStock(quantity: AvailableQuantity, product: Product)
