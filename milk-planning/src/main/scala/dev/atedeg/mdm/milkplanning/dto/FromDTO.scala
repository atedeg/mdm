package dev.atedeg.mdm.milkplanning.dto

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import cats.syntax.all.*
import eu.timepit.refined.numeric.{ NonNegative, Positive }

import dev.atedeg.mdm.milkplanning.{ Quantity, QuintalsOfMilk, RequestedProduct }
import dev.atedeg.mdm.milkplanning.IncomingEvent.*
import dev.atedeg.mdm.milkplanning.OutgoingEvent.*
import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.products.dto.toDomain as toProductDomain
import dev.atedeg.mdm.utils.*

extension (ro: ReceivedOrderDTO)
  def toDomain: Either[String, ReceivedOrder] =
    ro.products.toNel
      .toRight("The received order list is empty")
      .flatMap(_.traverse(_.toDomain))
      .map(ReceivedOrder.apply)

extension (rp: RequestedProductDTO)
  def toDomain: Either[String, RequestedProduct] =
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    for
      quantity <- rp.quantity.refined[Positive].map(Quantity.apply)
      product <- rp.product.toProductDomain
    yield RequestedProduct(product, quantity, LocalDateTime.parse(rp.requiredBy, formatter))

extension (om: OrderMilkDTO)
  def toDomain: Either[String, OrderMilk] =
    for quintalsOfMilk <- om.quintals.refined[NonNegative].map(QuintalsOfMilk.apply)
    yield OrderMilk(quintalsOfMilk)
