package dev.atedeg.mdm.clientorders

import dev.atedeg.mdm.products.Product

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:
  /**
   * An [[IncomingEvent event]] which is received when an [[order.IncomingOrder order]] is made.
   */
  case OrderReceived()

  /**
   * An [[IncomingEvent event]] received when an operator takes a [[Product product]] from the stock and palletizes it
   * for the given [[Order.InProgressOrder order]].
   */
  case ProductPalletizedForOrder(orderID: OrderID, quantity: Quantity, product: Product)

  /**
   * An [[IncomingEvent event]] received when an [[order.InProgressOrder order in progress]] is marked as
   * [[order.CompletedOrder ready to be shipped]].
   */
  case OrderCompleted(orderID: OrderID)
