package dev.atedeg.mdm.stocking.api.acl

import dev.atedeg.mdm.products.dto.ProductDTO
import dev.atedeg.mdm.stocking.dto.ProductRemovedFromStockDTO

final case class ProductPalletizedDTO(product: ProductDTO, quantity: Int)
extension (ppDTO: ProductPalletizedDTO)
  def toProductRemovedFromStockDTO: ProductRemovedFromStockDTO =
    ProductRemovedFromStockDTO(ppDTO.quantity, ppDTO.product)
