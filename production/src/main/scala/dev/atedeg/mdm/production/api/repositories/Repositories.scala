package dev.atedeg.mdm.production.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.*

trait RecipeBookRepository:
  def read[M[_]: Monad: LiftIO]: M[RecipeBookDTO]

trait ProductionsRepository:
  def writeInProgressProductions[M[_]: Monad: LiftIO](productions: List[InProgressDTO]): M[Unit]
