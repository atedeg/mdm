package dev.atedeg.mdm.production.api

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.StartProduction
import dev.atedeg.mdm.production.api.repositories.RecipeBookRepository
import dev.atedeg.mdm.production.dto.ProductionPlanReadyDTO
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def handleProductionPlanReady[M[_]: Monad: LiftIO: CanRead[RecipeBookRepository]: CanRaise[String]](
    ppr: ProductionPlanReadyDTO,
): M[Unit] =
  for
    config <- readState
    recipeBook <- config.recipeBookRepository.read >>= validate
    productionPlan <- validate(ppr).map(_.productionPlan)
    productions = setupProductions(productionPlan)
    val action: Action[MissingRecipe, StartProduction, NonEmptyList[Production.InProgress]] =
      productions.traverse(startProduction(recipeBook))
    (events, res) = action.execute
    _ <- events.traverse(config.emitter.emit)
    productions <- res.leftMap(_.toString).getOrRaise
    _ <- config.productionsRepository.write(productions.toDTO)
  yield ()
