package dev.atedeg.mdm.products.utils

import cats.syntax.all.*

import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.Ingredient.*
import dev.atedeg.mdm.utils.serialization.*

object ReadShowInstancesOps:

  given Show[Ingredient] with

    override def toShow(i: Ingredient): String = i match
      case Probiotics => "probiotics"
      case Rennet => "rennet"
      case Cream => "cream"
      case Milk => "milk"
      case Salt => "salt"

  given Read[Ingredient] with

    override def fromString(s: String): Either[String, Ingredient] = s match
      case "probiotics" => Probiotics.asRight[String]
      case "rennet" => Rennet.asRight[String]
      case "cream" => Cream.asRight[String]
      case "milk" => Milk.asRight[String]
      case "salt" => Salt.asRight[String]
      case _ => s"Unknown `Ingredient`: '$s'".asLeft[Ingredient]
