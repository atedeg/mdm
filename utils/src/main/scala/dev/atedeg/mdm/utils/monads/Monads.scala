package dev.atedeg.mdm.utils.monads

import cats.{ Monad, Traverse }
import cats.data.NonEmptyList
import cats.effect.{ IO, LiftIO }
import cats.mtl.{ Ask, Raise, Tell }
import cats.syntax.all.*

/**
 * Signals that a method could emit elements of a given type which are accumulated in a list.
 */
type CanEmit[Emitted] = [M[_]] =>> Tell[M, List[Emitted]]

/**
 * Signals that a method will always emit one or more elements of a given type which are accumulated in a list.
 * @note this check is not imposed neither at compile-time nor at run-time. This is just used to
 *       better document the behaviour of methods but does not guarantee any invariant.
 */
type Emits[Emitted] = [M[_]] =>> Tell[M, List[Emitted]]

/**
 * Signals that a method could fail with a given error type.
 */
type CanRaise[Raised] = [M[_]] =>> Raise[M, Raised]

/**
 * Signals that a method can read a state `C`.
 */
type CanRead[C] = [F[_]] =>> Ask[F, C]

/**
 * Emits an element of type `E` in a context `M[_]` that accumulates emitted elements in a list.
 */
def emit[M[_], E](e: E)(using T: Tell[M, List[E]]): M[Unit] = T.tell(List(e))

/**
 * Raises an error of type `E` in a context `M[_]` where the computation can be aborted.
 */
def raise[M[_], E, A](e: E)(using R: Raise[M, E]): M[A] = R.raise(e)

/**
 * Reads the current global state in a context `M[_]` with a global state `C`.
 */
def readState[C, M[_]: Monad: CanRead[C]](implicit A: Ask[M, C]): M[C] = A.ask

/**
 * Gets a view of the current state applying a function `f` to it.
 */
def readStateView[C, M[_]: Monad: CanRead[C], C1](f: C => C1): M[C1] = readState.map(f)

/**
 * `unless(cond)(a)` performs the monadic action `a` if the condition `cond` is false.
 */
def unless[M[_], A](cond: => Boolean)(action: => M[A])(using M: Monad[M]): M[Unit] = M.unlessA(cond)(action)

/**
 * `when(cond)(a)` performs the monadic action `a` if the condition `cond` holds true.
 */
def when[M[_], A](cond: => Boolean)(action: => M[A])(using M: Monad[M]): M[Unit] = M.whenA(cond)(action)

extension [A](a: IO[A]) def liftIO[F[_]](using L: LiftIO[F]): F[A] = L.liftIO(a)
extension [A](a: => A) def performSyncIO[F[_]](using L: LiftIO[F]): F[A] = IO(a).liftIO
extension [A, B](e: Either[A, B])
  def getOrRaise[M[_]: Monad: CanRaise[A]]: M[B] = e match
    case Left(err) => raise(err)
    case Right(res) => res.pure

extension [M[_]: Monad, A](ma: M[A])
  /**
   * `ma.thenReturn(b)` performs the monadic action `ma`, ignores its return value
   * and then returns the value `b` in the context `M[_]`.
   */
  def thenReturn[B](b: B): M[B] = ma *> b.pure

  /**
   * `ma.andThen(mb)` performs the monadic action `ma`, ignores its return value
   * and then performs the monadic action `mb`.
   */
  def andThen[B](mb: M[B]): M[B] = ma *> mb

  /**
   * `ma.void` performs the monadic action `ma` then discards its return value returning
   * `Unit` in the context `M[_]`.
   */
  def ignore: M[Unit] = ma *> ().pure

extension (condition: Boolean)
  /**
   * `cond.otherwiseRaise(err)` [[raise() raises]] the error `err` if the condition `cond` is false.
   */
  def otherwiseRaise[M[_], E](error: => E)(using R: Raise[M, E], M: Monad[M]): M[Boolean] =
    unless[M, Boolean](condition)(raise(error)).map(_ => condition)

extension [A](a: Option[A])
  /**
   * `opt.ifMissingRaise(err)` [[raise() raises]] the error `err` if `opt` is `None`,
   * otherwise returns its value inside the context `M[_]`.
   */
  def ifMissingRaise[M[_], E](error: => E)(using R: Raise[M, E], M: Monad[M]): M[A] = a match
    case Some(a) => a.pure
    case None => raise(error)

extension [T[_]: Traverse, A](as: T[A])
  /**
   * `as.forEachDo(ma)` performs the monadic action `ma` for each of the elements of `as`
   * and discards the return values returning `Unit` in the context `M[_]`.
   */
  def forEachDo[M[_]: Monad, B](f: A => M[B]): M[Unit] = as.traverse(f).void
