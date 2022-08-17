package dev.atedeg.mdm.pricing

import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList

import dev.atedeg.mdm.pricing.utils.*
import dev.atedeg.mdm.pricing.utils.given
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * A [[Product product]] with its ordered [[Quantity quantity]].
 */
final case class IncomingOrderLine(quantity: Quantity, product: Product)

/**
 * A quantity of something.
 */
final case class Quantity(n: PositiveNumber)

/**
 * Associates to each [[Product product]] its [[PriceInEuroCents unitary price]].
 */
final case class PriceList(priceList: Map[Product, PriceInEuroCents])

/**
 * A price expressed in cents, the smallest currency unit for euros.
 */
final case class PriceInEuroCents(n: PositiveNumber)

/**
 * A physical or legal entity that places [[IncomingOrderLine order lines]].
 */
final case class Client(code: ClientID)

/**
 * An ID which uniquely identifies a [[Client client]].
 */
final case class ClientID(id: UUID)

/**
 * A promotion for a [[Client client]], with an expiry date, containing promotion lines for some products.
 */
final case class Promotion(client: Client, expiryDate: LocalDateTime, lines: NonEmptyList[PromotionLine])

/**
 * A promotion line, which describes the [[Promotion promotion]].
 */
enum PromotionLine(val product: Product):
  /**
   * This promotion line specifies the discounted product and how much to discount it by.
   *
   * Every order line which contains the product is discounted by the specified amount.
   *
   * @note For example, if a 100g casatella normally costs 100 cents and the promotion line specifies
   * a 50% discount, each 100g casatella will cost 50 cents.
   */
  case Fixed(override val product: Product, discount: DiscountPercentage) extends PromotionLine(product)

  /**
   * This promotion line specifies the discounted product, the threshold and how much to discount it by.
   *
   * Only the products above the threshold are discounted; the other ones are at full price.
   *
   * @note For example, if a 100g casatella normally costs 100 cents and the promotion line specifies
   * a 50% discount above 5 casatellas, the first 5 casatella will cost 100 cents, while from the 6th onwards
   * they will cost 50 cents each.
   */
  case Threshold(override val product: Product, threshold: ThresholdQuantity, discount: DiscountPercentage)
      extends PromotionLine(product)

/**
 * A discount percentage, expressed as a number between 0 (exclusive) and 100 (inclusive).
 */
final case class DiscountPercentage(n: DecimalInOpenClosedRange[0.0, 1.0]) derives Times

/**
 * A threshold under which the discount does not apply.
 */
final case class ThresholdQuantity(n: PositiveNumber)
