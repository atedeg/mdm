package dev.atedeg.mdm.products.utils

import cats.syntax.all.*

import dev.atedeg.mdm.products.CheeseType
import dev.atedeg.mdm.products.CheeseType.*
import dev.atedeg.mdm.products.Ingredient
import dev.atedeg.mdm.products.Ingredient.*
import dev.atedeg.mdm.utils.serialization.*

object ReadShowInstances:
  given Show[CheeseType] with
    override def toShow(a: CheeseType): String = a match
      case Squacquerone => "squacquerone"
      case Stracchino => "stracchino"
      case Casatella => "casatella"
      case Caciotta => "caciotta"
      case Ricotta => "ricotta"

  given Read[CheeseType] with
    override def fromString(s: String): Either[String, CheeseType] = s match
      case "squacquerone" => Squacquerone.asRight[String]
      case "stracchino" => Stracchino.asRight[String]
      case "casatella" => Casatella.asRight[String]
      case "caciotta" => Caciotta.asRight[String]
      case "ricotta" => Ricotta.asRight[String]
      case _ => "Unknown `CheeseType`: '$s'".asLeft[CheeseType]

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
