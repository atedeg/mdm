package dev.atedeg.mdm.pricing

import java.time.LocalDateTime

import cats.data.NonEmptyList
import cats.syntax.all.*

import dev.atedeg.mdm.pricing.utils.{ percent as _, * }
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

/**
 * Computes the price of an order line, according to any promotion that may be applied for a certain client.
 */
@SuppressWarnings(Array("org.wartremover.warts.ListAppend"))
def priceOrderLine(
    priceList: PriceList,
    clientPromotions: List[Promotion],
    today: LocalDateTime,
)(orderLine: IncomingOrderLine): PriceInEuroCents =
  val IncomingOrderLine(quantity, product) = orderLine
  val baseUnitPrice = priceList.priceList(product)
  val activePromotions = clientPromotions.filter(_.expiryDate.isAfter(today))
  val lines = activePromotions.flatMap(_.lines.toList).filter(_.product === product)
  val fixedDiscounts = lines.collect { case p: PromotionLine.Fixed => p }
  val thresholdDiscounts = lines.collect { case p: PromotionLine.Threshold => p }
  val basePrice = PriceInEuroCents(baseUnitPrice.n * quantity.n)
  basePrice
    |> applyFixedDiscount(fixedDiscounts)
    |> applyThresholdDiscount(thresholdDiscounts, quantity)

private def applyFixedDiscount(promotionLines: List[PromotionLine.Fixed])(
    basePrice: PriceInEuroCents,
): PriceInEuroCents =
  promotionLines.map(_.discount).reduceOption(_ * _) match
    case Some(discount) => basePrice withDiscount discount
    case None => basePrice

private def applyThresholdDiscount(promotionLines: List[PromotionLine.Threshold], quantity: Quantity)(
    basePrice: PriceInEuroCents,
): PriceInEuroCents =
  def listToPair[A](l: List[A]): (A, A) = (l(1), l(0))

  val filteredLines = promotionLines
    .filter(_.threshold.n < quantity.n)
    .sortBy(_.threshold.n)
    .map(pl => (pl.threshold, pl.discount))
  if filteredLines.isEmpty
  then basePrice
  else
    val percentages = filteredLines.map(_._2.toPercentage.inverted).scan(100.0.percent)(_ * _)
    val ranges = filteredLines.map(_._1.n.value).prepended(0).appended(quantity.n.value)
    val distances = ranges.sliding(2).map(listToPair).map(_ - _)
    val multiplier = 1.0 - (percentages.zip(distances).map(_.value * _).sum / quantity.n.value)
    val discount = DiscountPercentage(coerce(multiplier))
    basePrice withDiscount discount
