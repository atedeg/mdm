package dev.atedeg.mdm.milkplanning.api

import dev.atedeg.mdm.milkplanning.api.emitters.OrderMilkEmitter
import dev.atedeg.mdm.milkplanning.api.repositories.*

final case class Configuration(
    receivedOrderRepository: ReceivedOrderRepository,
    recipeBookRepository: RecipeBookRepository,
    emitter: OrderMilkEmitter,
)
