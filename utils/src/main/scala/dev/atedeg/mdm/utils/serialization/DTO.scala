package dev.atedeg.mdm.utils.serialization

import java.time.{ LocalDate, LocalDateTime }
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.compiletime.*
import scala.deriving.*
import scala.util.Try

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.api.Refined

import dev.atedeg.mdm.utils.*

trait DTO[E, D]:
  def elemToDto(e: E): D
  def dtoToElem(dto: D): Either[String, E]

object DTO:
  extension [E](e: E) def toDTO[D](using d: DTO[E, D]) = d.elemToDto(e)
  extension [D](dto: D) def toDomain[E](using d: DTO[E, D]): Either[String, E] = d.dtoToElem(dto)

  given listDTO[E, D](using DTO[E, D]): DTO[List[E], List[D]] with
    override def dtoToElem(dto: List[D]): Either[String, List[E]] = dto.traverse(_.toDomain)
    override def elemToDto(e: List[E]): List[D] = e.map(_.toDTO)

  given nonEmptyListDTO[E, D](using DTO[E, D]): DTO[NonEmptyList[E], List[D]] with
    override def dtoToElem(dto: List[D]): Either[String, NonEmptyList[E]] =
      dto.toNel.toRight("Got an empty list, it should contain at least one element").flatMap(_.traverse(_.toDomain))

    override def elemToDto(e: NonEmptyList[E]): List[D] = e.toList.map(_.toDTO)

  given DTO[UUID, String] with
    override def dtoToElem(dto: String): Either[String, UUID] =
      Try(UUID.fromString(dto)).toEither.leftMap(_ => s"Invalid UUID: $dto")
    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    override def elemToDto(e: UUID): String = e.toString

  given DTO[LocalDate, String] with
    override def dtoToElem(dto: String): Either[String, LocalDate] =
      Try(LocalDate.parse(dto, DateTimeFormatter.ISO_LOCAL_DATE)).toEither.leftMap(_ => s"Invalid date: $dto")
    override def elemToDto(e: LocalDate): String = e.format(DateTimeFormatter.ISO_LOCAL_DATE)

  given DTO[LocalDateTime, String] with
    override def dtoToElem(dto: String): Either[String, LocalDateTime] =
      Try(LocalDateTime.parse(dto, DateTimeFormatter.ISO_LOCAL_DATE_TIME)).toEither.leftMap(_ => s"Invalid date: $dto")
    override def elemToDto(e: LocalDateTime): String = e.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  given refinedDTO[E, D, P: ValidFor[E]](using d: DTO[E, D]): DTO[E Refined P, D] with
    override def dtoToElem(dto: D): Either[String, E Refined P] = d.dtoToElem(dto).flatMap(_.refined[P])
    override def elemToDto(e: E Refined P): D = e.value.toDTO

  given DTO[Int, Int] = idDTO
  given DTO[Double, Double] = idDTO
  given DTO[String, String] = idDTO

  def idDTO[T]: DTO[T, T] = new DTO[T, T]:
    override def dtoToElem(dto: T): Either[String, T] = dto.asRight[String]
    override def elemToDto(e: T): T = e

  inline private def numberOfFields[A](inline p: Mirror.ProductOf[A]): Int = constValue[Tuple.Size[p.MirroredElemTypes]]
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  private def firstField[T, X](t: T): X = t.asInstanceOf[Product].productElement(0).asInstanceOf[X]

  private type First[T <: Tuple] = Tuple.Elem[T, 0]

  inline def caseClassDTO[E, D](using p: Mirror.ProductOf[E]): DTO[E, D] =
    inline numberOfFields(p) match
      case 1 =>
        type t = First[p.MirroredElemTypes]
        val instance: DTO[t, D] = summonInline[DTO[t, D]]
        new DTO[E, D]:
          def elemToDto(e: E): D = instance.elemToDto(firstField(e))
          def dtoToElem(dto: D): Either[String, E] = instance.dtoToElem(dto).map(e => p.fromProduct(e *: EmptyTuple))
      case _ => compiletime.error("Can only derive for case classes with only one field")

  inline def interCaseClassDTO[C1, C2](using p1: Mirror.ProductOf[C1])(using p2: Mirror.ProductOf[C2]): DTO[C1, C2] =
    inline if numberOfFields(p1) != numberOfFields(p2)
    then compiletime.error("Can only derive DTO for case classes with same number of fields")
    else
      type DTOFromTuple[T] = T match
        case (t1 *: t2 *: EmptyTuple) => DTO[t1, t2]
      type DTOs = Tuple.Map[Tuple.Zip[p1.MirroredElemTypes, p2.MirroredElemTypes], DTOFromTuple]
      val instances = summonAll[DTOs].toList.asInstanceOf[List[DTO[Any, Any]]]
      new DTO[C1, C2]:
        override def elemToDto(e: C1): C2 =
          val fields = e.asInstanceOf[Product].productIterator.zip(instances).map(fi => fi._2.elemToDto(fi._1)).toArray
          p2.fromProduct(Tuple.fromArray(fields))
        override def dtoToElem(dto: C2): Either[String, C1] = dto
          .asInstanceOf[Product]
          .productIterator
          .zip(instances)
          .toList
          .traverse(fi => fi._2.dtoToElem(fi._1))
          .map(l => p1.fromProduct(Tuple.fromArray(l.toArray)))
