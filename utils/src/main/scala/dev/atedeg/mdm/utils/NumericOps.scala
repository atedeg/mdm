package dev.atedeg.mdm.utils

trait Plus[N]:
  def plus(x: N, y: N): N
  extension (x: N) def +(y: N) = plus(x, y)

trait Times[N]:
  def times(x: N, y: N): N
  extension (x: N) def *(y: N) = times(x, y)

trait Minus[N]:
  def minus(x: N, y: N): N
  extension (x: N) def -(y: N) = minus(x, y)

trait Div[N]:
  def div(x: N, y: N): N
  extension (x: N) def /(y: N) = div(x, y)

object Plus:
  given [N: Numeric]: Plus[N] with
    override def plus(x: N, y: N): N = Numeric[N].plus(x, y)

object Times:
  given [N: Numeric]: Times[N] with
    override def times(x: N, y: N): N = Numeric[N].times(x, y)

object Minus:
  given [N: Numeric]: Minus[N] with
    override def minus(x: N, y: N): N = Numeric[N].minus(x, y)

object Div:
  given Div[Int] with
    override def div(x: Int, y: Int): Int = x / y

  given [N: Fractional]: Div[N] with
    override def div(x: N, y: N): N = Fractional[N].div(x, y)
