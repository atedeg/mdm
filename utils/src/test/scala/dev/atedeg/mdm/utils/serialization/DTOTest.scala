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
import org.scalatest.wordspec.AnyWordSpec
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
class Tests extends AnyWordSpec with ScalaCheckDrivenPropertyChecks with Matchers with Generators:
  extension [D](dto: D) def decodeAndEncode[E](using DTO[E, D]): D = dto.toDomain[E].value.toDTO[D]
  extension [E](elem: E) def encodeAndDecode[D](using DTO[E, D]): E = elem.toDTO[D].toDomain[E].value
  def encodingInverseOfDecoding[E, D](dto: D)(using DTO[E, D]): Assertion = dto.decodeAndEncode[E] shouldBe dto
  def decodingInverseOfEncoding[E, D](e: E)(using DTO[E, D]): Assertion = e.encodeAndDecode[D] shouldBe e
  extension [A, B](e: Either[A, B]) def shouldBeLeft: Assertion = e should matchPattern { case Left(_) => }

  "Default instances' decoding" should {
    "be the inverse of encoding" in {
      forAll(decodingInverseOfEncoding[Int, Int])
      forAll(decodingInverseOfEncoding[String, String])
      forAll(decodingInverseOfEncoding[Double, Double])
      forAll(decodingInverseOfEncoding[List[Int], List[Int]])
      forAll(nonEmpty)(decodingInverseOfEncoding[NonEmptyList[Int], List[Int]])
      forAll(uuid)(decodingInverseOfEncoding[UUID, String])
      forAll(localDate)(decodingInverseOfEncoding[LocalDate, String])
      forAll(localDateTime)(decodingInverseOfEncoding[LocalDateTime, String])
      forAll(positiveNumber)(decodingInverseOfEncoding[Int Refined Positive, Int])
    }
  }

  "Default instances' encoding" should {
    "be the inverse of decoding" in {
      forAll(encodingInverseOfDecoding[Int, Int])
      forAll(encodingInverseOfDecoding[String, String])
      forAll(encodingInverseOfDecoding[Double, Double])
      forAll(encodingInverseOfDecoding[List[Int], List[Int]])
      forAll(nonEmptyDTO)(encodingInverseOfDecoding[NonEmptyList[Int], List[Int]])
      forAll(uuidDTO)(encodingInverseOfDecoding[UUID, String])
      forAll(localDateDTO)(encodingInverseOfDecoding[LocalDate, String])
      forAll(localDateTimeDTO)(encodingInverseOfDecoding[LocalDateTime, String])
      forAll(positiveNumberDTO)(encodingInverseOfDecoding[Int Refined Positive, Int])
    }
  }

  "Default instances' invariants" should {
    "be checked when decoding" in {
      0.toDomain[Int Refined Positive].shouldBeLeft
      "invalid uuid".toDomain[UUID].shouldBeLeft
      "invalid date".toDomain[LocalDate].shouldBeLeft
      "invalid date time".toDomain[LocalDateTime].shouldBeLeft
      List[Int]().toDomain[NonEmptyList[Int]].shouldBeLeft
    }
  }

  "DTO auto-generation" when {
    "used for case class with one field" should {
      "generate a correct instance" in {
        given DTO[Test1, Int] = DTOGenerators.caseClassDTO
        forAll(test1)(decodingInverseOfEncoding[Test1, Int])
        forAll(arbitrary[Int])(encodingInverseOfDecoding[Test1, Int])
      }
      "be checked at compile-time" in {
        "val instance = DTOGenerators.caseClassDTO[Test1, Test2]" shouldNot compile
      }
    }
    "used with a pair of compatible case classes" should {
      "generate a correct instance" in {
        given DTO[Test1, Int] = DTOGenerators.caseClassDTO
        given DTO[Test2, Test2DTO] = DTOGenerators.interCaseClassDTO
        forAll(test2)(decodingInverseOfEncoding[Test2, Test2DTO])
        forAll(test2DTO)(encodingInverseOfDecoding[Test2, Test2DTO])
      }
      "be checked at compile-time" in {
        "val instance = DTOGenerators.interCaseClassDTO[Test1, Test2]" shouldNot compile
        "val instance = DTOGenerators.interCaseClassDTO[Test2, Test2DTO]" shouldNot compile
      }
    }
  }
