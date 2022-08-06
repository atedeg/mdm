package dev.atedeg.mdm.products.utils

import cats.syntax.all.*

import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.Ingredient.*
import dev.atedeg.mdm.utils.serialization.*

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
