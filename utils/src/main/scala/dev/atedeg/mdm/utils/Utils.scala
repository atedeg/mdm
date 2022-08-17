package dev.atedeg.mdm.utils

import cats.kernel.Order

def max[A: Order](a1: A, a2: A): A = Order[A].max(a1, a2)

extension [A](a: A) def |>[B](f: A => B): B = f(a)
