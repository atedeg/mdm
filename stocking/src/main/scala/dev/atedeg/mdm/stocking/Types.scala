package dev.atedeg.mdm.stocking

import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList

// FIXME: shared kernel
type Product = Int
type CheeseType = Int
type Quantity = Int
type PositiveDecimal = Double

/**
 * A batch of products of a certain [[CheeseType type]], uniquely identified by an [[BatchID ID]],
 * which hasn't been quality assured.
 */
enum Batch:
  /**
   * An aging batch that will become ready for quality assurance at the given date and time.
   */
  case Aging(id: BatchID, cheeseType: CheeseType, readyFrom: LocalDateTime)

  /**
   * A batch that is ready for quality assurance.
   */
  case ReadyForQualityAssurance(id: BatchID, cheeseType: CheeseType)

/**
 * A batch of products of a certain [[CheeseType type]] uniquely identified by an [[BatchID ID]],
 * which has undergone quality assurance.
 */
enum QualityAssuredBatch:
  /**
   * A batch which passed quality assurance.
   */
  case Passed(id: BatchID, cheeseType: CheeseType)

  /**
   * A batch which failed quality assurance.
   */
  case Failed(id: BatchID, cheeseType: CheeseType)

/**
 * Uniquely identifies a [[Batch batch]].
 */
final case class BatchID(id: UUID)

/**
 * A [[CheeseType cheese type]] with its respective [[Quantity quantity]] and the [[BatchID ID of the batch]] it belongs to.
 */
final case class LabelledProduct(cheeseType: CheeseType, quantity: Quantity, batchID: BatchID)

/**
 * A weight in grams reported by a scale.
 */
final case class WeightInGrams(grams: PositiveDecimal)
