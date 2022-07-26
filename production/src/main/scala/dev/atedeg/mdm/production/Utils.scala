package dev.atedeg.mdm.production

import scala.annotation.targetName

type PositiveDouble = Double // TODO: get from utils
final case class WeightInGrams(grams: PositiveDouble)
final case class WeightInQuintals(quintals: PositiveDouble)

extension (weight: WeightInGrams)
  def map(f: PositiveDouble => PositiveDouble): WeightInGrams = WeightInGrams(f(weight.grams))
  def toQuintals: WeightInQuintals = WeightInQuintals(weight.grams / 100_000)

extension (weight: WeightInQuintals)
  def map(f: PositiveDouble => PositiveDouble): WeightInQuintals = WeightInQuintals(f(weight.quintals))
  def toGrams: WeightInGrams = WeightInGrams(weight.quintals * 100_000)
  @targetName("multiply") def *(other: WeightInQuintals) = weight.map(_ * other.quintals)
  def of(ingredient: Ingredient): QuintalsOfIngredient = QuintalsOfIngredient(weight, ingredient)

extension (q: Quantity)
  @targetName("multiplyGrams") def *(weight: WeightInGrams): WeightInGrams = weight.map(_ * q)
  @targetName("multiplyQuintals") def *(weight: WeightInQuintals): WeightInQuintals = weight.map(_ * q)

extension (q: QuintalsOfIngredient)
  def map(f: PositiveDouble => PositiveDouble): QuintalsOfIngredient = q.quintals.map(f) of q.ingredient
  def *(w: WeightInQuintals): QuintalsOfIngredient = (w * q.quintals) of q.ingredient

extension (d: PositiveDouble)
  def quintals: WeightInQuintals = WeightInQuintals(d)