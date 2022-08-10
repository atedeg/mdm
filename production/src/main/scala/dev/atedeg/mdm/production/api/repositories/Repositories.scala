package dev.atedeg.mdm.production.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.*
import dev.atedeg.mdm.utils.monads.CanRaise

trait RecipeBookRepository:
  def read[M[_]: Monad: LiftIO]: M[RecipeBookDTO]

trait ProductionsRepository:
  def writeInProgressProductions[M[_]: Monad: LiftIO](productions: List[InProgressDTO]): M[Unit]
  def readInProgressProduction[M[_]: Monad: LiftIO: CanRaise[String]](productionID: String): M[InProgressDTO]
  def updateToEnded[M[_]: Monad: LiftIO](production: EndedDTO): M[Unit]

trait CheeseTypeRipeningDaysRepository:
  def read[M[_]: Monad: LiftIO]: M[CheeseTypeRipeningDaysDTO]
