package dev.atedeg.mdm.production.api

import dev.atedeg.mdm.production.api.repositories.RecipeBookRepository

case class Configuration(
    recipeBookRepository: RecipeBookRepository,
    productionsRepository: ProductionsRepository,
    emitter: StartProductionEmitter,
)
