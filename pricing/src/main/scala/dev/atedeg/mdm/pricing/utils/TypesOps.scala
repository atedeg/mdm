package dev.atedeg.mdm.pricing.utils

import dev.atedeg.mdm.pricing.*
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*

extension (n: NonNegativeNumber) def euroCents: PriceInEuroCents = PriceInEuroCents(n)

extension (n: PositiveNumber)
  def of(p: Product): IncomingOrderLine = IncomingOrderLine(Quantity(n), p)
  def quantity: Quantity = Quantity(n)
  def threshold: ThresholdQuantity = ThresholdQuantity(n)

extension (n: DecimalInOpenClosedRange[0.0, 100.0])
  def percent: DiscountPercentage = DiscountPercentage(coerce(n.value / 100.0))

extension (discount: DiscountPercentage) def toPercentage: Percentage = coerce(discount.n.value)

extension (price: PriceInEuroCents)
  def withDiscount(discount: DiscountPercentage): PriceInEuroCents = PriceInEuroCents(
    coerce(math.ceil(price.n.value * (1.0 - discount.n.value)).toInt),
  )
