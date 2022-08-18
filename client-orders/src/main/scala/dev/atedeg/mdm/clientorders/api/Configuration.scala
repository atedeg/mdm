package dev.atedeg.mdm.clientorders.api

import dev.atedeg.mdm.clientorders.api.repositories.*
import dev.atedeg.mdm.clientorders.api.services.PriceOrderLineService

final case class Configuration(
    priceOrderLineService: PriceOrderLineService,
    orderRepository: OrderRepository,
    emitter: Emitter,
)
