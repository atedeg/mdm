package dev.atedeg.mdm.production.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.production.dto.RecipeBookDTO

trait RecipeBookRepository:
  def read[M[_]: Monad: LiftIO]: M[RecipeBookDTO]

trait ProductionsRepository:
  def write[M[_]: Monad: LiftIO](production: ProductionDTO): M[?]
