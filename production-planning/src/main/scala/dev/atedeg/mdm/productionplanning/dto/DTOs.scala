package dev.atedeg.mdm.productionplanning.dto

type ProductDTO
// incoming events
final case class NewOrderReceivedDTO(order: OrderDTO)
final case class OrderDTO(orderID: String, requiredBy: String, orderedProducts: List[OrderedProductDTO])
final case class OrderedProductDTO(product: ProductDTO, quantity: Int)

// outgoing events
final case class ProductionPlanReadyDTO(productionPlan: ProductionPlanDTO)
final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
final case class ProductToProduceDTO(product: ProductDTO, quantity: Int)

final case class OrderDelayed(orderID: String, newDeliveryDate: String)
