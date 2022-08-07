package dev.atedeg.mdm.utils.serialization

import java.time.{ LocalDate, LocalDateTime, ZoneId }
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.collection.mutable
import scala.language.postfixOps

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.predicates.all.Positive
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.uuid
import org.scalacheck.Prop.forAll
import org.scalacheck.util.Buildable
import org.scalatest.*
import org.scalatest.EitherValues.*
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import dev.atedeg.mdm.utils.coerce
import dev.atedeg.mdm.utils.serialization.DTOOps.*

final case class Test1(n: Int)
final case class Test2(t: Test1, s: String)
final case class Test2DTO(t: Int, s: String)

trait Generators:
  val test1: Gen[Test1] = arbitrary[Int].map(Test1.apply)
  val test1DTO: Gen[Int] = arbitrary[Int]
  val test2: Gen[Test2] = test1.flatMap(t1 => arbitrary[String].map(Test2(t1, _)))
  val test2DTO: Gen[Test2DTO] = arbitrary[Int].flatMap(i => arbitrary[String].map(Test2DTO(i, _)))
  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val nonEmpty: Gen[NonEmptyList[Int]] = Gen.nonEmptyListOf(arbitrary[Int]).map(_.toNel.get)
  val nonEmptyDTO: Gen[List[Int]] = nonEmpty.map(_.toList)
  val localDateTime: Gen[LocalDateTime] = Gen.calendar.map(_.toInstant.atZone(ZoneId.systemDefault).toLocalDateTime)
  val localDateTimeDTO: Gen[String] = localDateTime.map(_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  val localDate: Gen[LocalDate] = localDateTime.map(_.toLocalDate)
  val localDateDTO: Gen[String] = localDate.map(_.format(DateTimeFormatter.ISO_LOCAL_DATE))
  val positiveNumber: Gen[Int Refined Positive] = Gen.posNum[Int].map(coerce)
  val positiveNumberDTO: Gen[Int] = Gen.posNum[Int]
  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  val uuidDTO: Gen[String] = uuid.map(_.toString)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
class Tests extends AnyFeatureSpec with GivenWhenThen with ScalaCheckDrivenPropertyChecks with Matchers with Generators:
  extension [D](dto: D) def decodeAndEncode[E](using DTO[E, D]): D = dto.toDomain[E].value.toDTO[D]
  extension [E](elem: E) def encodeAndDecode[D](using DTO[E, D]): E = elem.toDTO[D].toDomain[E].value
  def encodingInverseOfDecoding[E, D](dto: D)(using DTO[E, D]): Assertion = dto.decodeAndEncode[E] shouldBe dto
  def decodingInverseOfEncoding[E, D](e: E)(using DTO[E, D]): Assertion = e.encodeAndDecode[D] shouldBe e
  extension [A, B](e: Either[A, B]) def shouldBeLeft: Assertion = e should matchPattern { case Left(_) => }

  Feature("Default instances' decoding is the inverse of encoding") {
    Scenario("Int")(forAll(decodingInverseOfEncoding[Int, Int]))
    Scenario("String")(forAll(decodingInverseOfEncoding[String, String]))
    Scenario("Double")(forAll(decodingInverseOfEncoding[Double, Double]))
    Scenario("List")(forAll(decodingInverseOfEncoding[List[Int], List[Int]]))
    Scenario("NonEmptyList")(forAll(nonEmpty)(decodingInverseOfEncoding[NonEmptyList[Int], List[Int]]))
    Scenario("UUID")(forAll(uuid)(decodingInverseOfEncoding[UUID, String]))
    Scenario("LocalDate")(forAll(localDate)(decodingInverseOfEncoding[LocalDate, String]))
    Scenario("LocalDateTime")(forAll(localDateTime)(decodingInverseOfEncoding[LocalDateTime, String]))
    Scenario("Refined positive")(forAll(positiveNumber)(decodingInverseOfEncoding[Int Refined Positive, Int]))
  }

  Feature("Default instances' encoding is the inverse of decoding") {
    Scenario("Int")(forAll(encodingInverseOfDecoding[Int, Int]))
    Scenario("String")(forAll(encodingInverseOfDecoding[String, String]))
    Scenario("Double")(forAll(encodingInverseOfDecoding[Double, Double]))
    Scenario("List")(forAll(encodingInverseOfDecoding[List[Int], List[Int]]))
    Scenario("NonEmptyList")(forAll(nonEmptyDTO)(encodingInverseOfDecoding[NonEmptyList[Int], List[Int]]))
    Scenario("UUID")(forAll(uuidDTO)(encodingInverseOfDecoding[UUID, String]))
    Scenario("LocalDate")(forAll(localDateDTO)(encodingInverseOfDecoding[LocalDate, String]))
    Scenario("LocalDateTime")(forAll(localDateTimeDTO)(encodingInverseOfDecoding[LocalDateTime, String]))
    Scenario("Refined positive")(forAll(positiveNumberDTO)(encodingInverseOfDecoding[Int Refined Positive, Int]))
  }

  Feature("Default instances' invariants are checked when decoding") {
    Scenario("Refined positive")(0.toDomain[Int Refined Positive] shouldBeLeft)
    Scenario("UUID")("invalid uuid".toDomain[UUID] shouldBeLeft)
    Scenario("LocalDate")("invalid date".toDomain[LocalDate] shouldBeLeft)
    Scenario("LocalDateTime")("invalid date time".toDomain[LocalDateTime] shouldBeLeft)
    Scenario("NonEmptyList")(List[Int]().toDomain[NonEmptyList[Int]] shouldBeLeft)
  }

  Feature("DTO auto generation") {
    Scenario("Generating a DTO for a case class with one field") {
      Given("a case class")
      When("it has only one field")
      And("it has a DTO instance")
      Then("it should be possible to auto derive a DTO instance")
      given DTO[Test1, Int] = DTOGenerators.caseClassDTO
      And("decoding is the inverse of encoding")
      forAll(test1)(decodingInverseOfEncoding[Test1, Int])
      And("vice versa")
      forAll(arbitrary[Int])(encodingInverseOfDecoding[Test1, Int])
    }

    Scenario("Generating a DTO between two compatible case classes") {
      Given("two case classes")
      When("they have the same number of fields")
      And("there are instances to convert between fields")
      given DTO[Test1, Int] = DTOGenerators.caseClassDTO
      Then("it should be possible to auto derive a DTO instance")
      given DTO[Test2, Test2DTO] = DTOGenerators.interCaseClassDTO
      And("decoding is the inverse of encoding")
      forAll(test2)(decodingInverseOfEncoding[Test2, Test2DTO])
      And("vice versa")
      forAll(test2DTO)(encodingInverseOfDecoding[Test2, Test2DTO])
    }
  }

  Feature("Compile-time checks during auto generation") {
    Scenario("Generating a DTO between case classes with a different number of fields") {
      Given("two case classes")
      When("they have a different number of fields")
      Then("it should be impossible to auto derive a DTO instance")
      "val instance = DTOGenerators.interCaseClassDTO[Test1, Test2]" shouldNot compile
    }

    Scenario("Generating a DTO between case classes with fields that can not converted with a DTO instance") {
      Given("two case classes")
      When("they have the same number of fields")
      And("there is no DTO instance to convert between fields")
      Then("it should be impossible to auto derive a DTO instance")
      "val instance = DTOGenerators.interCaseClassDTO[Test2, Test2DTO]" shouldNot compile
    }

    Scenario("Generating a DTO for a case class with one field that can not be converted with a DTO instance") {
      Given("a case class")
      When("it has only one field")
      And("it does not have a DTO instance for that field")
      Then("it should be impossible to auto derive a DTO instance")
      "val instance = DTOGenerators.caseClassDTO[Test1, Test2]" shouldNot compile
    }
  }
