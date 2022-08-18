package dev.atedeg.mdm.pricing.api.repositories

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.pricing.dto.*

trait PriceListRepository:
  def read[M[_]: Monad: LiftIO]: M[PriceListDTO]

trait PromotionsRepository:
  def readByClientID[M[_]: Monad: LiftIO](clientID: String): M[List[PromotionDTO]]

final case class PriceListRepositoryDB(connectionString: String) extends PriceListRepository:
  override def read[M[_]: Monad: LiftIO]: M[PriceListDTO] = ???

final case class PromotionsRepositoryDB(connectionString: String) extends PromotionsRepository:
  override def readByClientID[M[_]: Monad: LiftIO](clientID: String): M[List[PromotionDTO]] = ???
