package dev.atedeg.mdm.pricing.api

import dev.atedeg.mdm.pricing.api.repositories.*

final case class Configuration(
    priceListRepository: PriceListRepository,
)
