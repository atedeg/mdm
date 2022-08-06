package dev.atedeg.mdm.milkplanning.dto

import dev.atedeg.mdm.products.dto.ProductDTO

final case class ReceivedOrderDTO(products: List[RequestedProductDTO])
final case class RequestedProductDTO(product: ProductDTO, quantity: Int, requiredBy: String)
final case class OrderMilkDTO(quintals: Int)
