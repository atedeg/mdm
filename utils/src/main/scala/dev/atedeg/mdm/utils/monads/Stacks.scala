package dev.atedeg.mdm.utils.monads

import cats.data.{ EitherT, Reader, ReaderT, Writer, WriterT }
import cats.effect.IO

private type Reading[State] = [A] =>> Reader[State, A]
private type EmittingT[M[_], Event] = [A] =>> WriterT[M, List[Event], A]

/**
 * An action that can read an immutable state of type `State`, emits one or more events of type `Event` and
 * can either fail with an error of type `Error` or produces a value of type `Result`.
 */
type ActionWithState[Error, Event, Result, State] = EitherT[EmittingT[Reading[State], Event], Error, Result]

/**
 * The same as an [[ActionWithState action with state]] but does not read an immutable state.
 */
type Action[Error, Event, Result] = ActionWithState[Error, Event, Result, Unit]

/**
 * The same as an [[Action action]] but does not fail.
 */
type SafeAction[Event, Result] = Writer[List[Event], Result]

/**
 * The same as a [[SafeAction safe action]] but with two events.
 */
type SafeActionTwoEvents[Event1, Event2, Result] = WriterT[[A] =>> Writer[List[Event2], A], List[Event1], Result]

/**
 * An action which performs `IO`, reads an immutable state `C` and can either fail with an error `E`
 * or produce a result `R`.
 */
type ServerAction[C, E, R] = EitherT[[A] =>> ReaderT[IO, C, A], E, R]

extension [Event1, Event2, Result](action: SafeActionTwoEvents[Event1, Event2, Result])
  def execute: (List[Event1], List[Event2], Result) =
    val (e2, (e1, res)) = action.run.run
    (e1, e2, res)

extension [Event, Result](action: SafeAction[Event, Result]) def execute: (List[Event], Result) = action.run

extension [Error, Event, Result, State](action: ActionWithState[Error, Event, Result, State])
  /**
   * `a.execute(s)` runs the [[ActionWithState action]] `a` with a state `s` returning all the
   * emitted events and its return value.
   */
  def execute(state: State): (List[Event], Either[Error, Result]) = action.value.run(state)

extension [Error, Event, Result](action: Action[Error, Event, Result])
  /**
   * `a.execute(s)` runs the [[Action action]] a returning all the emitted events and its return value.
   */
  def execute: (List[Event], Either[Error, Result]) = action.value.run(())
