package dev.atedeg.mdm.utils.serialization

import java.time.{ LocalDate, LocalDateTime }
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

import cats.data.NonEmptyList
import cats.syntax.all.*
import eu.timepit.refined.api.Refined

import dev.atedeg.mdm.utils.*

trait DTO[E, D]:
  def elemToDto(e: E): D
  def dtoToElem(dto: D): Either[String, E]

  extension (e: E) def toDTO: D = elemToDto(e)
  extension (dto: D) def toDomain: Either[String, E] = dtoToElem(dto)

object DTO:
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

  private def idDTO[T]: DTO[T, T] = new DTO[T, T]:
    override def dtoToElem(dto: T): Either[String, T] = dto.asRight[String]
    override def elemToDto(e: T): T = e
