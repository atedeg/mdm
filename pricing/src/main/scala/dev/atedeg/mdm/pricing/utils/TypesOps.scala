package dev.atedeg.mdm.pricing.utils

import dev.atedeg.mdm.pricing.*
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.utils.*

extension (n: PositiveNumber)
  def euroCents: PriceInEuroCents = PriceInEuroCents(n)
  def of(p: Product): IncomingOrderLine = IncomingOrderLine(Quantity(n), p)
  def quantity: Quantity = Quantity(n)

extension (n: DecimalInOpenClosedRange[0.0, 100.0]) def percent: DiscountPercentage = DiscountPercentage(n)
