package dev.atedeg.mdm.clientorders.utils

import cats.kernel.Order
import eu.timepit.refined.auto.autoUnwrap

import dev.atedeg.mdm.clientorders.PalletizedQuantity
import dev.atedeg.mdm.clientorders.PalletizedQuantity.apply
import dev.atedeg.mdm.clientorders.PriceInEuroCents
import dev.atedeg.mdm.clientorders.Quantity
import dev.atedeg.mdm.utils.*
import dev.atedeg.mdm.utils.given

extension (n: PositiveNumber) def euroCents: PriceInEuroCents = PriceInEuroCents(n)
extension (i: NonNegativeNumber) def palletizedQuantity: PalletizedQuantity = PalletizedQuantity(i)

object QuantityOps:
  extension (q: Quantity) def toPalletizedQuantity: PalletizedQuantity = PalletizedQuantity(q.n)

  given Order[Quantity] with
    override def compare(x: Quantity, y: Quantity): Int = Order[Int].compare(x.n, y.n)
