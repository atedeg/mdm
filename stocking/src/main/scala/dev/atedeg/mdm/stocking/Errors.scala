package dev.atedeg.mdm.stocking

import dev.atedeg.mdm.products.Product

/**
 * The errors that have to be handled by the bounded context.
 */
enum Errors:
  /**
   * An error raised by the [[labelProduct() labelling action]] if the actual weight is too far
   * from the expected weights.
   */
  case WeightNotInRange(expectedWeight: WeightInGrams, actualWeight: WeightInGrams)

  /**
   * An error raised by the [[removeFromStock() removal from stock action]] if the quantity to be removed from stock
   * exceeds the available one
   */
  case NotEnoughStock(product: Product, triedQuantity: AvailableQuantity, actualQuantity: AvailableQuantity)