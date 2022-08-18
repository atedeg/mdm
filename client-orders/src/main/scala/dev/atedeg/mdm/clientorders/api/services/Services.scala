package dev.atedeg.mdm.clientorders.api.services

import cats.Monad
import cats.effect.LiftIO

import dev.atedeg.mdm.clientorders.dto.*

trait PriceOrderLineService:
  def getOrderLinePrice[M[_]: Monad: LiftIO](clientID: String, orderLine: IncomingOrderLineDTO): M[PriceInEuroCentsDTO]

final class PriceOrderLineServiceHTTP extends PriceOrderLineService:
  override def getOrderLinePrice[M[_]: Monad: LiftIO](
      clientID: String,
      orderLine: IncomingOrderLineDTO,
  ): M[PriceInEuroCentsDTO] = ???
