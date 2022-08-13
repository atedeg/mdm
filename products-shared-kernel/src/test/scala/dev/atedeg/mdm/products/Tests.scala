package dev.atedeg.mdm.products

import cats.syntax.all.*
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.oneOf
import org.scalatest.Assertion
import org.scalatest.matchers.dsl.DefinedWord
import org.scalatest.matchers.dsl.EmptyWord
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import dev.atedeg.mdm.products.CheeseType.*
import dev.atedeg.mdm.products.utils.*

class Tests extends AnyWordSpec, ScalaCheckDrivenPropertyChecks, Matchers:
  extension [A](a: Option[A])
    def shouldBeDefinedIf(b: Boolean): Assertion = if b then a shouldBe defined else a shouldBe empty

  def generatorIncluding(l: List[Int]): Gen[Int] = Gen.oneOf(Gen.oneOf(l), arbitrary[Int])
  def checkWeights(cheeseType: CheeseType): Assertion =
    val weights = cheeseType.allowedWeights.map(_.n.value).toList
    val gen = generatorIncluding(weights)
    forAll(gen)(w => cheeseType withWeight (_ === w) shouldBeDefinedIf weights.contains(w))

  "The `withWeight` extension method" should {
    "create a product only if its weight is correct" in {
      checkWeights(Ricotta)
      checkWeights(Squacquerone)
      checkWeights(Casatella)
      checkWeights(Stracchino)
      checkWeights(Caciotta)
    }
  }
