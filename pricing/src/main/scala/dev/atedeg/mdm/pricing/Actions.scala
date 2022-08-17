package dev.atedeg.mdm.pricing

import java.time.LocalDateTime

import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.pricing.utils.{ percent as _, * }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

@SuppressWarnings(Array("org.wartremover.warts.ListAppend"))
def priceOrderLine(
    priceList: PriceList,
    clientPromotions: List[Promotion],
    today: LocalDateTime,
)(orderLine: IncomingOrderLine): PriceInEuroCents =
  val (quantity, product) = (orderLine.quantity, orderLine.product)
  val baseUnitPrice = priceList.priceList(product)
  val activePromotions = clientPromotions.filter(_.expiryDate.isAfter(today))
  val lines = activePromotions.flatMap(_.lines.toList).filter(_.product === product)
  // Get the fixed discounts
  val fixedDiscounts = lines.collect { case p @ PromotionLine.Fixed(_, _) => p }
  // Get the threshold discounts
  val thresholdDiscounts = lines.collect { case p @ PromotionLine.Threshold(_, _, _) => p }
  val basePrice = PriceInEuroCents(baseUnitPrice.n * quantity.n)
  basePrice
    |> applyFixedDiscount(fixedDiscounts)
    |> applyThresholdDiscount(thresholdDiscounts, quantity)

private def applyFixedDiscount(promotionLines: List[PromotionLine.Fixed])(
    basePrice: PriceInEuroCents,
): PriceInEuroCents =
  val maybeDiscount = promotionLines.map(_.discount).reduceOption(_ * _)
  maybeDiscount match
    case Some(discount) => basePrice withDiscount discount
    case None => basePrice

private def applyThresholdDiscount(promotionLines: List[PromotionLine.Threshold], quantity: Quantity)(
    basePrice: PriceInEuroCents,
): PriceInEuroCents =
  val filteredLines = promotionLines.collect {
    case PromotionLine.Threshold(_, threshold, discount) if threshold.n.value < quantity.n.value =>
      (threshold, discount)
  }.sortBy(_._1.n.value)
  if filteredLines.isEmpty
  then basePrice
  else
    val percentages = filteredLines.map(_._2.toPercentage.inverted).scan(100.0.percent)(_ * _)
    val ranges = filteredLines.map(_._1.n.value).prepended(0).appended(quantity.n.value)
    val distances = ranges.drop(1).zip(ranges).map(_ - _)
    val multiplier = 1.0 - percentages.zip(distances).map(_.value * _).sum / quantity.n.value
    val discount = DiscountPercentage(coerce(multiplier))
    basePrice withDiscount discount
