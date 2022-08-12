package dev.atedeg.mdm.clientorders.api

import dev.atedeg.mdm.clientorders.api.repositories.*

final case class Configuration(
    priceListRepository: PriceListRepository,
    orderRepository: OrderRepository,
    emitter: Emitter,
)
