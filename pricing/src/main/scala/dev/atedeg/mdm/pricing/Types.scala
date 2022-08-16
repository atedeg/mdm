package dev.atedeg.mdm.pricing

import java.util.UUID
import java.time.LocalDateTime

import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given
import cats.data.NonEmptyList

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
final case class PriceInEuroCents(n: PositiveNumber) derives Plus

/**
 * A physical or legal entity that places [[IncomingOrderLine order lines]].
 */
final case class Client(code: ClientID)

/**
 * An ID which uniquely identifies a [[Client client]].
 */
final case class ClientID(id: UUID)

/**
 * A list of all the promotions for each [[Client client]].
 */
final case class PromotionsList(promotions: Map[Client, NonEmptyList[Promotion]])

/**
 * A promotion for a [[Client client]], with an expiry date.
 * It can either be fixed or by threshold.
 * If it is fixed, a product is discounted by a fixed amount;
 * if it is by threshold, only the products above the threshold are discounted.
 */
enum Promotion:
    /**
     * A fixed promotion.
     */
    case Fixed(client: Client, expiryDate: LocalDateTime, lines: NonEmptyList[PromotionLine.Fixed])
    /**
     * A threshold promotion.
     */
    case Threshold(client: Client, expiryDate: LocalDateTime, lines: NonEmptyList[PromotionLine.Threshold])

/**
 * A promotion line, which describes either the
 * [[Promotion.Fixed fixed]] or the [[Promotion.Threshold threshold]] promotion.
 */
enum PromotionLine:
    /**
     * A line that describes part of a [[Promotion.Fixed fixed promotion]].
     * It contains the discounted product and how much to discount it by.
     * 
     * Every order line which contains the product is discounted by the specified amount.
     * 
     * @note For example, if a 100g casatella normally costs 100 cents and the promotion line specifies
     * a 50% discount, each 100g casatella will cost 50 cents.
     */
    case Fixed(product: Product, discount: DiscountPercentage)
    /**
      * A line that describes part of a [[Promotion.Threshold threshold promotion]].
      * It contains the discounted product, the threshold and how much to discount it by.
      * 
      * Only the products above the threshold are discounted; the other ones are at full price.
      *
      * @note For example, if a 100g casatella normally costs 100 cents and the promotion line specifies
      * a 50% discount above 5 casatellas, the first 5 casatella will cost 100 cents, while from the 6th onwards
      * they will cost 50 cents each.
      */
    case Threshold(product: Product, threshold: Quantity, discount: DiscountPercentage)

/**
 * A discount percentage, expressed as a number between 0 (exclusive) and 100 (inclusive).
 */
final case class DiscountPercentage(n: DecimalInOpenClosedRange[0, 100])
