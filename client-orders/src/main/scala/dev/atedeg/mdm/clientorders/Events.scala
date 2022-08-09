package dev.atedeg.mdm.clientorders

import java.time.LocalDateTime

import cats.data.NonEmptyList

import dev.atedeg.mdm.products.Product

/**
 * The events that have to be handled by the bounded context.
 */
enum IncomingEvent:

  /**
   * An [[IncomingEvent event]] which is received when an [[Order.IncomingOrder order]] is made.
   */
  case OrderReceived(
      id: OrderID,
      orderLines: NonEmptyList[IncomingOrderLine],
      customer: Customer,
      deliveryDate: LocalDateTime,
      deliveryLocation: Location,
  )

  /**
   * An [[IncomingEvent event]] received when an operator takes a [[Product product]] from the stock and palletizes it
   * for the given [[Order.InProgressOrder order]].
   */
  case ProductPalletizedForOrder(orderID: OrderID, quantity: Quantity, product: Product)

  /**
   * An [[IncomingEvent event]] received when an [[Order.InProgressOrder order in progress]] is marked as
   * [[order.CompletedOrder ready to be shipped]].
   */
  case OrderCompleted(orderID: OrderID)

/**
 * The events that may be produced by the bounded context.
 */
enum OutgoingEvent:
  /**
   * An event emitted when a new [[IncomingOrder incoming order]] is received and processed.
   */
  case OrderProcessed(incomingOrder: IncomingOrder)

  /**
   * An event emitted when a [[Product product]] is successfully palletized for an [[Order.InProgressoOrder order]].
   */
  case ProductPalletized(product: Product, quantity: Quantity)
