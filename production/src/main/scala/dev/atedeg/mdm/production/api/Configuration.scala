package dev.atedeg.mdm.production.api

import dev.atedeg.mdm.production.api.emitters.*
import dev.atedeg.mdm.production.api.repositories.*

final case class Configuration(
    recipeBookRepository: RecipeBookRepository,
    productionsRepository: ProductionsRepository,
    ripeningDaysRepository: CheeseTypeRipeningDaysRepository,
    emitter: Emitter,
)
