package dev.atedeg.mdm.productionplanning.api

import dev.atedeg.mdm.productionplanning.api.emitters.*
import dev.atedeg.mdm.productionplanning.api.repositories.*

final case class Configuration(
    receivedOrderRepository: ReceivedOrderRepository,
    ripeningDaysRepository: RipeningDaysRepository,
    productionPlanReadyEmitter: ProductionPlanReadyEmitter,
    orderDelayedEmitter: OrderDelayedEmitter,
)
