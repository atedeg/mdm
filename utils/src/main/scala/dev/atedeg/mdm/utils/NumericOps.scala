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
  def toCeil(n: N): N
  extension (n: N) def ceil: N = toCeil(n)

trait Distance[N]:
  def distance(x: N, y: N): N
  extension (x: N) def distanceFrom(y: N) = distance(x, y)

given [N: Numeric]: Plus[N] with
  override def plus(x: N, y: N): N = Numeric[N].plus(x, y)

object Plus:

  inline def derived[A](using gen: K0.ProductGeneric[A]): Plus[A] = plusGen

  inline given plusGen[N](using inst: K0.ProductInstances[Plus, N]): Plus[N] with
    def plus(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (p: Plus[n], n1: n, n2: n) => p.plus(n1, n2))

given [N: Numeric]: Times[N] with
  override def times(x: N, y: N): N = Numeric[N].times(x, y)

object Times:

  inline def derived[A](using gen: K0.ProductGeneric[A]): Times[A] = timesGen

  inline given timesGen[N](using inst: K0.ProductInstances[Times, N]): Times[N] with
    def times(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (t: Times[n], n1: n, n2: n) => t.times(n1, n2))

given [N: Numeric]: Minus[N] with
  override def minus(x: N, y: N): N = Numeric[N].minus(x, y)

object Minus:

  inline def derived[A](using gen: K0.ProductGeneric[A]): Minus[A] = minusGen

  inline given minusGen[N](using inst: K0.ProductInstances[Minus, N]): Minus[N] with
    def minus(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (m: Minus[n], n1: n, n2: n) => m.minus(n1, n2))

given Div[Int] with
  override def div(x: Int, y: Int): Int = x / y

given [N: Fractional]: Div[N] with
  override def div(x: N, y: N): N = Fractional[N].div(x, y)

object Div:

  inline def derived[A](using gen: K0.ProductGeneric[A]): Div[A] = divGen

  inline given divGen[N](using inst: K0.ProductInstances[Div, N]): Div[N] with
    def div(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (d: Div[n], n1: n, n2: n) => d.div(n1, n2))

given [N](using N: Numeric[N]): Distance[N] with
  override def distance(x: N, y: N): N = N.abs(N.minus(x, y))

object Distance:

  inline def derived[A](using gen: K0.ProductGeneric[A]): Distance[A] = distanceGen

  inline given distanceGen[N](using inst: K0.ProductInstances[Distance, N]): Distance[N] with
    def distance(n1: N, n2: N): N = inst.map2(n1, n2)([n] => (m: Distance[n], n1: n, n2: n) => m.distance(n1, n2))

given Ceil[Double] with
  override def toCeil(n: Double): Double = math.ceil(n)

object Ceil:
  inline def derived[A](using gen: K0.ProductGeneric[A]): Ceil[A] = ceilGen

  inline given ceilGen[N](using inst: K0.ProductInstances[Ceil, N]): Ceil[N] with
    override def toCeil(n: N): N = inst.map(n)([n] => (c: Ceil[n], n: n) => c.toCeil(n))
