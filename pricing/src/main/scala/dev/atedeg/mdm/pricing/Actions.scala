package dev.atedeg.mdm.pricing

import java.time.LocalDateTime

import cats.syntax.all.*

import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

@SuppressWarnings(Array("org.wartremover.warts.ListAppend"))
def priceOrderLine(
    priceList: PriceList,
    promotionsList: PromotionsList,
    today: LocalDateTime,
)(client: Client, orderLine: IncomingOrderLine): PriceInEuroCents =
  val (quantity, product) = (orderLine.quantity, orderLine.product)
  val basePrice = priceList.priceList(product)
  val clientPromotions = promotionsList.promotions.get(client) match
    case Some(promotions) => promotions.toList
    case None => List.empty[Promotion]
  val activePromotions = clientPromotions.filter(_.expiryDate.isAfter(today))
  val lines = activePromotions.flatMap(_.lines.toList).filter(_.product === product)
  // Accumulate the fixed discounts
  val fixedDiscount = lines.collect { case PromotionLine.Fixed(_, discount) =>
    (100.0 - discount.n.value) / 100.0
  }.foldRight(1.0)(_ * _)
  // Get how many products fall into which thresholds
  val (thresholds, discounts) = lines.collect {
    case PromotionLine.Threshold(_, threshold, discount) if threshold.n.value < quantity.n.value =>
      (threshold.n.value, (100.0 - discount.n.value) / 100.0)
  }.sorted.unzip
  val chunks = 0 +: thresholds :+ quantity.n.value
  val chunksSizes = chunks.drop(1).zip(chunks).map(_ - _)
  val chunksDiscounts = 1.0 +: discounts
  val thresholdDiscount = chunksSizes.zip(chunksDiscounts).foldRight(0.0)((elem, acc) => (acc + elem._1) * elem._2)
  val orderLinePrice = basePrice.n.value * fixedDiscount * thresholdDiscount
  PriceInEuroCents(coerce(math.ceil(orderLinePrice).toInt))
