package dev.atedeg.mdm.utils

import scala.annotation.targetName

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{ NonNegative, Positive }
import shapeless3.deriving.K0

trait Plus[N]:
  def plus(x: N, y: N): N
  extension (x: N) @targetName("plusOperator") def +(y: N) = plus(x, y)

trait Times[N]:
  def times(x: N, y: N): N
  extension (x: N) @targetName("timesOperator") def *(y: N) = times(x, y)

trait Minus[N]:
  def minus(x: N, y: N): N
  extension (x: N) @targetName("minusOperator") def -(y: N) = minus(x, y)

trait Div[N]:
  def div(x: N, y: N): N
  extension (x: N) @targetName("divOperator") def /(y: N) = div(x, y)

trait Ceil[N]:
  def ceil(n: N): N
  extension (n: N) def toCeil: N = ceil(n)

object Plus:

  given [N: Numeric]: Plus[N] with
    override def plus(x: N, y: N): N = Numeric[N].plus(x, y)
  inline def derived[A](using gen: K0.ProductGeneric[A]): Plus[A] = plusGen

  inline given plusGen[N](using inst: K0.ProductInstances[Plus, N]): Plus[N] with
    def plus(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (p: Plus[n], n1: n, n2: n) => p.plus(n1, n2))

object Times:

  given [N: Numeric]: Times[N] with
    override def times(x: N, y: N): N = Numeric[N].times(x, y)
  inline def derived[A](using gen: K0.ProductGeneric[A]): Times[A] = timesGen

  inline given timesGen[N](using inst: K0.ProductInstances[Times, N]): Times[N] with
    def times(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (t: Times[n], n1: n, n2: n) => t.times(n1, n2))

object Minus:

  given [N: Numeric]: Minus[N] with
    override def minus(x: N, y: N): N = Numeric[N].minus(x, y)
  inline def derived[A](using gen: K0.ProductGeneric[A]): Minus[A] = minusGen

  inline given minusGen[N](using inst: K0.ProductInstances[Minus, N]): Minus[N] with
    def minus(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (m: Minus[n], n1: n, n2: n) => m.minus(n1, n2))

object Div:

  given Div[Int] with
    override def div(x: Int, y: Int): Int = x / y

  given [N: Fractional]: Div[N] with
    override def div(x: N, y: N): N = Fractional[N].div(x, y)

  inline def derived[A](using gen: K0.ProductGeneric[A]): Div[A] = divGen

  inline given divGen[N](using inst: K0.ProductInstances[Div, N]): Div[N] with
    def div(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (d: Div[n], n1: n, n2: n) => d.div(n1, n2))

given [N, P <: Positive | NonNegative: ValidFor[N]](using C: Ceil[N]): Ceil[N Refined P] with
  override def ceil(n: N Refined P): N Refined P = coerce(C.ceil(n.value))

given Ceil[Double] with
  override def ceil(n: Double): Double = math.ceil(n)

object Ceil:
  inline def derived[A](using gen: K0.ProductGeneric[A]): Ceil[A] = ceilGen

  inline given ceilGen[N](using inst: K0.ProductInstances[Ceil, N]): Ceil[N] with
    override def ceil(n: N): N = inst.map(n)([n] => (c: Ceil[n], n: n) => c.ceil(n))
