package dev.atedeg.mdm.production.api

import cats.Monad
import cats.data.NonEmptyList
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.production.*
import dev.atedeg.mdm.production.IncomingEvent.*
import dev.atedeg.mdm.production.OutgoingEvent.{ ProductionEnded, StartProduction }
import dev.atedeg.mdm.production.api.repositories.RecipeBookRepository
import dev.atedeg.mdm.production.dto.*
import dev.atedeg.mdm.production.dto.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def handleProductionPlanReady[M[_]: Monad: LiftIO: CanRead[Configuration]: CanRaise[String]](
    ppr: ProductionPlanReadyDTO,
): M[Unit] =
  for
    config <- readState
    recipeBook <- config.recipeBookRepository.read >>= validate
    productionPlan <- validate(ppr).map(_.productionPlan)
    productions = setupProductions(productionPlan)
    action: Action[MissingRecipe, StartProduction, NonEmptyList[Production.InProgress]] =
      productions.traverse(startProduction(recipeBook))
    (events, res) = action.execute
    _ <- events.map(_.toDTO[StartProductionDTO]).traverse(config.emitter.emitStart)
    productions <- res.leftMap(m => s"Missing recipe: $m").getOrRaise
    _ <- config.productionsRepository.writeInProgressProductions(productions.toDTO)
  yield ()

def handleProductionEnded[M[_]: Monad: LiftIO: CanRead[Configuration]: CanRaise[String]](
    productionEnded: IncomingProductionEndedDTO,
): M[Unit] =
  for
    config <- readState
    productionID <- validate(productionEnded).map(_.productionID)
    production <- config.productionsRepository.readInProgressProduction(productionID.toDTO) >>= validate
    action: SafeAction[ProductionEnded, Production.Ended] = endProduction(production)
    (events, result) = action.execute
    _ <- events.map(_.toDTO[ProductionEndedDTO]).traverse(config.emitter.emitEnded)
    _ <- config.productionsRepository.updateToEnded(result.toDTO[EndedDTO])
  yield ()
