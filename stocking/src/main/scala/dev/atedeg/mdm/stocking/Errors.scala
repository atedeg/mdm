package dev.atedeg.mdm.stocking

import dev.atedeg.mdm.products.*

/**
 * The errors that have to be handled by the bounded context.
 */
enum Error:
  /**
   * An error raised by the [[labelProduct() labelling action]] if the actual weight is too far
   * from the expected weight.
   */
  case WeightNotInRange(expectedWeight: Grams, actualWeight: Grams)

  /**
   * An error raised by the [[removeFromStock() removal from stock action]] if the quantity to be removed from stock
   * exceeds the available one.
   */
  case NotEnoughStock(product: Product, triedQuantity: DesiredQuantity, actualQuantity: AvailableQuantity)
