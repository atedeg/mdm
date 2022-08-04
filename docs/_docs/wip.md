---
layout: static-site-main
---
# WIP

## CI

- Cose da elencare sicuramente:
  - Conventional commit
  - Tutte le diverse automazioni

## Quality assurance // FIXME

- Scalafix
- Wartremover
  - Tutti i wart abilitati di default fatta eccezione per
    - Equals (da errori strani in scala 3)
    - Overload (per avere codice più leggibile)
- Scalafmt stile comune enforced automaticamente
- Coverage con Jacoco (+ Codecov per github)
- Check di sonarcloud su tutte le PR

## Code development

### Design approach

While we were furthering our knowledge to better approach the development of the project we stumbled upon a very
interesting [talk](https://www.youtube.com/watch?v=2JB1_e5wZmU) by Scott Wlaschin and later read the book
[_"Domain Modelling Made Functional"_](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/).
This sparked a keen interest in the topic and convinced us to carry out the project by embracing a fully functional
approach.

All domain concepts are modelled using bare-bone enums, case classes and type aliases; every action dealing with these
entities is either defined as simple functions, or as extension methods.
This allowed us to have _self-contained, clutter-free_ and _simple_ files containing all the definitions of the main
domain concepts; this way, we were able to skim through these files along with the domain experts to get precious
feedback we could easily use to rework our ubiquitous language on the spot.

### Action modelling

All core domain actions take advantage of a _monadic encoding of side effects,_ ranging from failure with an exception,
to emitting events to reading an immutable global state.

Using monads to model side effects proved useful in three distinct ways:

- The core logic of the resulting code is pretty _easy to read and understand:_ complex actions are modelled using a
  DSL for effects and can be easily composed together. Moreover, these functions expose the side effects they can
  perform in their type signature making it _impossible for the programmer to forget to handle them_
- By reifying side effects as data we were able to _easily test_ the core behaviour of our system
- This approach is a _good fit to implement the hexagonal architecture:_ all the core logic is pure and does not
  perform any kind of IO
  ([_dependency rejection_](https://blog.ploeh.dk/2017/01/27/from-dependency-injection-to-dependency-rejection/))
  while all necessary dependencies are injected as simple
  parameters or in the reader monad. Following this discipline makes a hexagonal architecture emerge quite naturally

> A code example from our codebase:
>
> ```scala
> def labelProduct[M[_]: CanRaise[WeightNotInRange]: CanEmit[ProductStocked]: Monad](...): M[LabelledProduct] =
> for {
>   ...
>   product <- optionalProduct.ifMissingRaise(WeightNotInRange(...): WeightNotInRange)
>   labelledProduct = LabelledProduct(product, AvailableQuantity(1), batch.id)
>   _ <- emit(ProductStocked(labelledProduct): ProductStocked)
> } yield labelledProduct
> ```
>
> Just by reading the type signature one knows that the execution of the function can raise a
> `WeightNotInRange` error and can emit `ProductStocked` events.
>
> `ifMissingRaise` and `emit` are a part of the monadic DSL we devised to write more concise
> and easy-to-read code: thanks to these functions and for comprehension we can compose a
> sequence of small actions to obtain more complex behaviour.

### Make illegal states unrepresentable

Before starting the development of the project we also decided to fully embrace the
_"make illegal states unrepresentable"_ philosophy while leveraging the features the Scala's type system could offer.
First of all, according to the DDD principles, all domain elements are modelled using appropriate data structures to
wrap primitive types.
We also used algebraic data types (Scala 3's enums and case classes) to model in an effective way the domain's concepts
and constraints:

```scala
// A batch of cheese that can either be aging or ready for quality assurance
enum Batch:
  case Aging(id: BatchID, cheeseType: CheeseType, readyFrom: LocalDateTime)
  case ReadyForQualityAssurance(id: BatchID, cheeseType: CheeseType)

// We do not use flags or booleans that could lead to confusing representations
// like this one:
final case class Batch(
  id: BatchID,
  cheeseType: CheeseType,
  isAging: Bool,
  readyFrom: Option[LocalDateTime], // only defined if isAging 
)
```

Lastly, in order to further expand the static guarantees that our code could have, we decided to extensively use
[refinement types](https://github.com/fthomas/refined).
Primitive types -- and especially primitive numeric types like Int and Double -- are not only wrapped inside value
objects but also refined with compile-time checked predicates. The main advantages we obtained from this approach were:

- many invariants are made explicit directly in the types making it easier to understand how the code works
- the programmer can not inadvertently mix the types or break the invariants since these are checked by the compiler
- fewer tests to write
- better modelling of core domain concepts

> As a practical example: we want to model the concept of a stocked quantity of a cheese; of course it does not
> make sense for this quantity to be a negative number. A simple value object for this concept could look like this:
>
> ```scala
> final case class InStockQuantity private(n: Int)
> object InStockQuantity:
>   def apply(n: Int): Option[InStockQuantity] =
>     if n < 0 then None else Some(InStockQuantity(n))
> ```
>
> However, this is critical: the core invariant that states that `n` must be positive is not immediately apparent
> from the definition of `InStockQuantity`; the programmer must read the builder implementation to understand that
> negative numbers are not allowed.
> It is not guaranteed that the builder will always make sure that the invariant holds, so it is necessary to write
> unit tests to ensure that no accidental changes to the `apply` method can break the invariant.
>
> Using refinement types:
>
> ```scala
> type PositiveNumber = Int Refined Positive
> final case class InStockQuantity(n: PositiveNumber)
> ```
>
> There's no need to have a builder that enforces the positive invariant: `n` is guaranteed to be correct by the types.
> This code is also self-documenting: by simply reading the definition it is immediately clear that `n` must be
> positive; there's no need to go and read the builder (actually there's no need to have a separate builder at all!
> The default `apply` method of the case class is more than enough)
>
> Even better: we do not have to write a single test to check that `InStockQuantity` is built correctly since the
> compiler will reject any code where the programmer can not prove that `n` is indeed positive!

## Documentation

Documentation plays a fundamental role in our codebase: every entity --be it a case class, enum or type alias--
mirrors a ubiquitous language concept. To make sure that the code and the ubiquitous language always evolve together
we decided that the code should be the only source of truth: each and every ubiquitous language concept should
appear in the code and the code should not use words that do not belong to the ubiquitous language.

This way the code __is__ the ubiquitous language: there are no additional documents describing the ubiquitous
language that could go stale and have to be actively kept in sync with the code.
Moreover, trying to change the code definitions forces the programmer to think about the ubiquitous language and discuss
these changes with the domain experts.
Lastly, knowledge crunching with the domain experts consists in writing simple data structure definitions; in our
experience the domain expert quickly became familiar with the syntax and learned to "ignore" it to
focus on the correctness of the definitions we were writing as we spoke.

> One of our first meetings to knowledge crunch with the domain experts went like this:
>
> __Domain expert:__ ... a type of cheese is either Ricotta or Caciotta
>
> _Meanwhile, we write something like this:_
>
> ```scala
> enum CheeseType:
>   case Ricotta
>   case Caciotta 
> ```
>
> __Domain expert:__ what is that enum word?
>
> __Programmer:__ it is just a way to say that a cheese type can be any of the following alternatives: so, just like you
> said, a cheese type is either a Ricotta or a Caciotta

This approach allowed for a tight feedback loop where the experts could look at the code and check that it actually
mirrored their understanding of the domain.

### Challenges

In order to use this approach we had to face a couple of challenges:

1. The code describing the ubiquitous language had to be readable by the domain experts with little help from the
   programmers; this way it's possible to quickly skim through the code along with the domain experts to make sure
   it faithfully mirrors the concepts of the domain
2. There should be a way to automatically generate a textual description of the ubiquitous language starting from the
   code, this way it is guaranteed to always be in sync with the code

As described above (TODO: link al paragrafo precedente se possibile) the first problem was addressed by keeping
the modelling of the domain concepts as easy as possible.

As for the second problem, we developed a [sbt plugin](https://github.com/atedeg/sbt-ubiquitous-scaladoc) that,
by parsing the documentation generated by unidoc, automatically generates Markdown tables with the desired elements of
the ubiquitous language.
