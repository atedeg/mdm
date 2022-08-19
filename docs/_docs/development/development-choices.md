---
title: Development choices
layout: static-site-main
---

# Development choices

In this section we are going to describe some of the more relevant choices we made when implementing the
clean architecture described in the previous chapter, both from core domain and application layer
perspective.

## Core Domain

### Domain Modelling Approach

While we were furthering our knowledge to better approach the development of the project we stumbled
upon a very interesting [talk](https://www.youtube.com/watch?v=2JB1_e5wZmU) by Scott Wlaschin and
later read the book [_"Domain Modelling Made Functional"_](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/).
This sparked a keen interest in the topic and convinced us to carry out the project by embracing a
fully functional approach.

All domain concepts are modelled using bare-bone enums and case classes; every action dealing with
these entities is either defined as simple functions or as extension methods.
This allowed us to have _self-contained, clutter-free_ and _simple_ files containing all the
definitions of the main domain concepts; this way, we were able to skim through these files along
with the domain experts to get precious feedback we could easily use to rework our ubiquitous
language on the spot.

### Action modelling through monads

All core domain actions take advantage of a _monadic encoding of side effects,_ ranging from failure
with an exception, to emitting events, to reading an immutable global state.

Using monads to model side effects proved useful in three distinct ways:

- The core logic of the resulting code is pretty _easy to read and understand:_ complex actions are
  modelled using a DSL for effects and can be easily composed together.
  Moreover, these functions expose the side effects they can perform in their type signature making
  it _impossible for the programmer to forget to handle them_
- By reifying side effects as data we were able to _easily test_ the core behaviour of our system
- This approach is a _good fit to implement the clean architecture:_ all the core logic is pure
  and does not perform any kind of IO
  ([_dependency rejection_](https://blog.ploeh.dk/2017/01/27/from-dependency-injection-to-dependency-rejection/))
  while all necessary dependencies are injected as simple parameters or in the reader monad.
  Following this discipline makes a clean architecture emerge quite naturally

> A code example from our codebase:
>
> ```scala
> def labelProduct[M[_]: CanRaise[WeightNotInRange]: CanEmit[ProductStocked]: Monad](...): M[LabelledProduct] =
> for
>   ...
>   product <- optionalProduct.ifMissingRaise(WeightNotInRange(...): WeightNotInRange)
>   labelledProduct = LabelledProduct(product, AvailableQuantity(1), batch.id)
>   _ <- emit(ProductStocked(labelledProduct): ProductStocked)
> yield labelledProduct
> ```
>
> Just by reading the type signature one knows that the execution of the function can raise a
> `WeightNotInRange` error and can emit `ProductStocked` events.
>
> `ifMissingRaise` and `emit` are a part of the monadic DSL we devised to write more concise
> and easy-to-read code: thanks to these functions and Scala's for comprehension we can compose a
> sequence of small actions to obtain more complex behaviour.

### Make illegal states unrepresentable

Before starting the development of the project we also decided to fully embrace the
_"make illegal states unrepresentable"_ philosophy while leveraging the features the Scala's
type system could offer.
First of all, according to the DDD principles, all domain elements are modelled using appropriate
data structures to wrap primitive types.
We also used [Scala 3's ADTs](https://docs.scala-lang.org/scala3/book/types-adts-gadts.html)
to model effectively the domain's concepts and constraints:

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

Lastly, to further expand the static guarantees that our code could have, we decided to extensively
use [refinement types](https://github.com/fthomas/refined).
Primitive types -- and especially primitive numeric types like `Int` and `Double` -- are not only
wrapped inside value objects but also refined with compile-time checked predicates.
The main advantages we obtained from this approach were:

- many invariants are made explicit directly in the types making it easier to understand how the
  code works
- the programmer can not inadvertently mix the types or break the invariants since these are checked
  by the compiler
- fewer tests to write
- better modelling of core domain concepts

> As a practical example: we want to model the concept of a stocked quantity of a cheese; of course
> it does not make sense for this quantity to be a negative number. A simple value object for this
> concept could look like this:
>
> ```scala
> final case class InStockQuantity private(n: Int)
> object InStockQuantity:
>   def apply(n: Int): Option[InStockQuantity] =
>     if n < 0 then None else Some(InStockQuantity(n))
> ```
>
> However, this is critical: the core invariant that states that `n` must be positive is not
> immediately apparent from the definition of `InStockQuantity`; the programmer must read the
> builder implementation to understand that negative numbers are not allowed.
> It is not guaranteed that the builder will always make sure that the invariant holds, so it is
> necessary to write unit tests to ensure that no accidental changes to the `apply` method can
> break the invariant.
>
> Using refinement types:
>
> ```scala
> type PositiveNumber = Int Refined Positive
> final case class InStockQuantity(n: PositiveNumber)
> ```
>
> There's no need to have a builder that enforces the positive invariant: `n` is guaranteed to be
> correct by the types.
> This code is also self-documenting: by simply reading the definition it is immediately clear that
> `n` must be positive; there's no need to go and read the builder (actually there's no need to have
> a separate builder at all! The default `apply` method of the case class is more than enough)
>
> Even better: we do not have to write a single test to check that `InStockQuantity` is built
> correctly since the compiler will reject any code where the programmer can not prove that `n` is
> indeed positive.

## Application Layer

### DTOs

The DTOs play a fundamental role in interacting with external microservices and the persistence layer.
However, the code to convert a DTO to a domain model object and vice-versa is often trivial and follows a
simple pattern that lends itself to being automatically generated via meta-programming.

First, we defined a `DTO[E, D]` type class to describe the predicate that an element of type `E` has a DTO
of type `D` and a conversion between the two can be performed. We also wrote some basic instances for
base types such as `DTO[Int, Int]` or `DTO[String, String]` (meaning that base types like `Int` and `String`
are already considered as DTOs).
With this simple setup we started defining the conversion methods but quickly realized the code was very repetitive;
consider the following example:

> Consider the following core domain concepts:
>
> ```scala
> final case class ProductionPlan(plan: NonEmptyList[ProductionPlanItem])
> final case class ProductionPlanItem(productToProduce: Product, units: NumberOfUnits)
> ```
>
> And their DTOs:
>
> ```scala
> final case class ProductionPlanDTO(productsToProduce: List[ProductToProduceDTO])
> final case class ProductToProduceDTO(product: ProductDTO, units: Int)
> ```
>
> The DTOs closely mirror the case class structure but only use easy-to-serialize primitive types,
> simple collections or other DTOs.
> The encoding/decoding code would simply encode/decode each individual field recursively using the
> appropriate `DTO` instances. The code would repeat in the exact same way for each domain case class:
>
> ```scala
> given DTO[DomainCaseClass, DTOCaseClass] with
>   def dtoToDomain(dto: DTOCaseClass): Either[String, DomainCaseClass] = for 
>     field1 <- dto.field1.decode(using DTO[TypeOfField1, TypeOfDTOField1])
>     ... 
>     fieldN <- dto.fieldN.decode(using DTO[TypeOfFieldN, TypeOfDTOFieldN])
>   yield DomainCaseClass(field1, ..., fieldN)
>  def domainToDto(domain: DomainCaseClass): DTOCaseClass = DTOCaseClass(
>    domain.field1.encode(using DTO[TypeOfField1, TypeOfDTOField1]),
>    ...,
>    domain.fieldN.encode(using DTO[TypeOfFieldN, TypeOfDTOFieldN]),
>   )
> ```
>
> We devised some methods to automatically generate this boilerplate-y code using some of Scala
> 3's meta programming capabilities:
>
> ```scala
> given DTO[DomainCaseClass, DTOCaseClass] = productTypeDTO
> ```

### HTTP API

Some bounded contexts required implementing an HTTP API, we decided to leverage
the [tapir](https://tapir.softwaremill.com/en/latest/) library.
It provided many useful features:

- It integrates nicely with the [cats](https://typelevel.org/cats/) library
  allowing us to keep writing monadic code using the `IO` monad
- It makes it possible to declaratively describe an endpoint and its associated
  route in a type-safe way
- It automatically generates the [OpenAPI](https://openapi.it/) specification
  and enables a [Swagger](https://swagger.io/) endpoint

After providing a declarative description of the API, implementing the server is as
simple as providing a function with the described input and output using the `IO`
monad.
Therefore, we described the server logic using functions parametrized on any monad
that can perform IO operations. This had the added benefit of allowing us
to easily test the server logic mocking all the accesses to the database.
