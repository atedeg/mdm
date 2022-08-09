package dev.atedeg.mdm.milkplanning.api.repositories

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.milkplanning.QuintalsOfMilk
import dev.atedeg.mdm.milkplanning.dto.{ ReceivedOrderDTO, RecipeBookDTO, RequestedProductDTO }

trait RecipeBookRepository:
  def getRecipeBook[M[_]: Monad: LiftIO]: M[RecipeBookDTO]

trait ReceivedOrderRepository:
  def save[M[_]: Monad: LiftIO](requestedProducts: List[RequestedProductDTO]): M[Unit]
  def getRequestedProducts[M[_]: Monad: LiftIO]: M[List[RequestedProductDTO]]
