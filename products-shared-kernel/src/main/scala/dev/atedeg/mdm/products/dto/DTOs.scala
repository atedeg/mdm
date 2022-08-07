package dev.atedeg.mdm.products.dto

import cats.syntax.all.*

import dev.atedeg.mdm.products.{ CheeseType, Grams, Product }
import dev.atedeg.mdm.products.CheeseType.*
import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.Ingredient.*
import dev.atedeg.mdm.products.utils.withWeight
import dev.atedeg.mdm.utils.serialization.DTO
import dev.atedeg.mdm.utils.serialization.DTOGenerators.*
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class ProductDTO(cheeseType: String, weight: Int)

object ProductDTO:
  import dev.atedeg.mdm.products.dto.CheeseTypeDTO.given
  given DTO[Product, ProductDTO] with
    override def dtoToElem(dto: ProductDTO): Either[String, Product] =
      for
        cheeseType <- dto.cheeseType.toDomain[CheeseType]
        weight <- dto.weight.toDomain[Grams]
        product <- cheeseType
          .withWeight(_ === weight.n.value)
          .toRight(s"No product of type $cheeseType with weight $weight")
      yield product

    override def elemToDto(e: Product): ProductDTO = ProductDTO(e.cheeseType.toDTO, e.weight.toDTO)
  private given DTO[Grams, Int] = caseClassDTO

object CheeseTypeDTO:
  given DTO[CheeseType, String] with
    override def dtoToElem(dto: String): Either[String, CheeseType] = dto match
      case "squacquerone" => Squacquerone.asRight[String]
      case "stracchino" => Stracchino.asRight[String]
      case "casatella" => Casatella.asRight[String]
      case "caciotta" => Caciotta.asRight[String]
      case "ricotta" => Ricotta.asRight[String]
      case _ => "Unknown `CheeseType`: '$s'".asLeft[CheeseType]

    override def elemToDto(e: CheeseType): String = e match
      case Squacquerone => "squacquerone"
      case Stracchino => "stracchino"
      case Casatella => "casatella"
      case Caciotta => "caciotta"
      case Ricotta => "ricotta"

object IngredientDTO:
  given DTO[Ingredient, String] with
    override def dtoToElem(dto: String): Either[String, Ingredient] = dto match
      case "probiotics" => Probiotics.asRight[String]
      case "rennet" => Rennet.asRight[String]
      case "cream" => Cream.asRight[String]
      case "milk" => Milk.asRight[String]
      case "salt" => Salt.asRight[String]
      case _ => s"Unknown `Ingredient`: '$dto'".asLeft[Ingredient]

    override def elemToDto(e: Ingredient): String = e match
      case Probiotics => "probiotics"
      case Rennet => "rennet"
      case Cream => "cream"
      case Milk => "milk"
      case Salt => "salt"
