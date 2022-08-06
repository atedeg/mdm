package dev.atedeg.mdm.products.dto

import dev.atedeg.mdm.products.Product
import dev.atedeg.mdm.products.utils.ReadShowInstances.given

extension (p: Product) def toDTO: ProductDTO = ProductDTO(p.cheeseType.show, p.weight.n.value)
