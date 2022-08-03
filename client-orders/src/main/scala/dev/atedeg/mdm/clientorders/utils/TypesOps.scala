package dev.atedeg.mdm.clientorders.utils

import cats.kernel.Order
import eu.timepit.refined.auto.autoUnwrap

import dev.atedeg.mdm.clientorders.*
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

extension (n: PositiveNumber)
  def euroCents: PriceInEuroCents = PriceInEuroCents(n)
  def quantity: Quantity = Quantity(n)

extension (i: NonNegativeNumber)
  def palletizedQuantity: PalletizedQuantity = PalletizedQuantity(i)
  def missingQuantity: MissingQuantity = MissingQuantity(i)

object QuantityOps:

  extension (q: Quantity)
    def toPalletizedQuantity: PalletizedQuantity = PalletizedQuantity(q.n)
    def toMissingQuantity: MissingQuantity = MissingQuantity(q.n)

  given Order[Quantity] with
    override def compare(x: Quantity, y: Quantity): Int = Order[Int].compare(x.n, y.n)
