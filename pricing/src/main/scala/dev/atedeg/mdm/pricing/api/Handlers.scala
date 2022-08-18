package dev.atedeg.mdm.pricing.api

import java.time.LocalDateTime

import cats.Monad
import cats.effect.LiftIO
import cats.syntax.all.*

import dev.atedeg.mdm.pricing.*
import dev.atedeg.mdm.pricing.dto.*
import dev.atedeg.mdm.pricing.dto.given
import dev.atedeg.mdm.utils.monads.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

def priceOrderLineHandler[M[_]: Monad: LiftIO: CanRaise[String]: CanRead[Configuration]](
    clientID: String,
    orderLineDTO: IncomingOrderLineDTO,
): M[PriceInEuroCentsDTO] =
  for
    config <- readState
    incomingOrderLine <- validate(orderLineDTO)
    priceList <- config.priceListRepository.read >>= validate
    promotions <- validate[String, ClientID, M](clientID).map(_.toDTO)
      >>= config.promotionsRepository.readByClientID
      >>= validate[List[PromotionDTO], List[Promotion], M]
    now <- LocalDateTime.now.performSyncIO
    price = priceOrderLine(priceList, promotions, now)(incomingOrderLine)
  yield price.toDTO
