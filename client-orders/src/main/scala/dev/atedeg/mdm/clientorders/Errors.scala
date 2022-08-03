package dev.atedeg.mdm.clientorders

import dev.atedeg.mdm.products.Product

/**
 * An error that may be produced by the [[palletizeProductForOrder palletization action]].
 */
enum PalletizationError:
  /**
   * Raised when trying to palletize a [[Product product]] for an [[Order.InProgressOrder order]] that does not require
   * it.
   */
  case ProductNotInOrder()

  /**
   * Raised when trying to palletize more of a [[Product product]] than required.
   */
  case PalletizedMoreThanRequired(requiredQuantity: MissingQuantity)

/**
 * An error that may be produced by the [[completeOrder() order completion action]].
 */
enum OrderCompletionError:
  /**
   * Raised when trying to mark an [[Order.InProgressOrder order]] as [[Order.CompletedOrder completed]] when any of its
   * [[Order.InProgressOrderLine lines]] are not completed.
   */
  case OrderNotComplete()
