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
While we were furthering our knowledge to better approach the development of the project we stumbled upon a very interesting talk by Scott Wlaschin [3] and later read the book _"Domain Modelling Made Functional"_ [4]. This sparked a keen interest in the topic and convinced us to carry out the project by embracing a fully functional approach.

All domain concepts are modelled using bare-bone enums, case classes and type aliases; every action dealing with these entities is either defined as simple functions, or as extension methods. This allowed us to have _self-contained, clutter-free_ and _simple_ files containing all the definitions of the main domain concepts; this way, we were able to skim through these files along with the domain experts to get precious feedback we could easily use to rework our ubiquitous language on the spot.

### Action modelling
All core domain actions take advantage of a _monadic encoding of side-effects,_ ranging from failure with an exception, to emitting events to reading an immutable global state.

Using monads to model side-effects proved useful in three distinct ways:
- The core logic of the resulting code is pretty _easy to read and understand:_ complex actions are modelled using a DSL for effects and can be easily composed together. Moreover, these functions expose the side-effects they can perform in their type signature making it _impossible for the programmer to forget to handle them_
- By reifying side-effects as data we were able to _easily test_ the core behaviour of our system
- This approach is a _good fit to implement the hexagonal architecture:_ all the core logic is pure and does not perform any kind of IO (_dependency rejection_ [1]) while all necessary dependencies are injected as simple parameters or in the reader monad. Following this discipline makes an hexagonal architechture emerge quite naturally

### Make illegal states unrepresentable
Before starting the development of the project we also decided to fully embrace the _"make illegal states unrepresentable"_ philosophy while leveraging the features the Scala's type system could offer.
First of all, according to the DDD principles, all domain elements are modelled using appropriate data structures to wrap primitive types.
We also used algebraic data types (Scala 3's enums and case classes) to model in an effective way the domain's concepts and constraints:

```
esempio di codice (per esempio l'ordine dove i diversi stati sono modellati con un ADT e non con un flag che può portare a inconsistenze)
```

Lastly, in order to further expand the static guarantees that our code could have, we deecided to extensively use refinement type [2].
Primitive types -- and especially primitive numeric types like Int and Double -- are not only wrapped inside value objects but also refined with compile-time checked predicates. The main advantages we obtained from this approach were:
- many invariants are made explicit directly in the types making it easier to understand how the code works
- the programmer can not inadvertently mix the types or break the invariants since these are checked by the compiler
- fewer tests to write
- better modelling of core domain concepts

```
// Simple value object, this is not enough
InStockQuantity(n: Int)
// Better modelling: 
InStockQuantity(n: PositiveNumber)

// simply reading the definition we know that
// it must be a positive number

// the compiler won't allow us to build an
// InStockQuantity if we can not prove that n
// is indeed positive

// We do not have to write any test to check
// that the factory methods to build an 
// InStockQuantity work correctly and fail
// when we pass it a negative integer since that
// can not happen!


Esempio: prendere esempio dal codice dove questo è evidente, probabilmente un punto dove usiamo i positiveNumber e nonNegativeNumber dove sarebbe facile confondere i due se non si fa attenzione (mi sembra che nel BC di Nicolas ci fosse un bel punto dove questo si vedeva)
```

## Documentation
Documentation plays a fundamental role in our codebase: every entity --be it a case class, enum or type alias-- mirrors a ubiquitous language concept. There should not be domain entities in our code that do not belong to the ubiquitous language and vice-versa. To make sure that the code and the ubiquitous language always evolve together we decided that the code should be the only source of truth: each and every ubiquitous language concept should appear in the code and the code should not use words that do not belong to the ubiquitous language.

Despite having to overcome some challenges, this approach proved to be extremly useful:
- since the code and the ubiquitous language are the same thing, reworking the ubiquitous language definitions along with the help of domain experts consists in changing the code
- there is no need to have separate textual documents to keep track of the ubiquitous language; the code __is__ the ubiquituos language and there is no risk of having other documents that could go out of sync with the code
- trying to change the code definition forces the programmer to think about the ubiquituos language and discuss these changes with the domain experts

### Challenges
In order to use this approach we had to face a couple of challenges:
1. The code describing the ubiquitous language had to be readable by the domain experts with little help from the programmers; this way it's possible to quickly skim through the code along with the domain experts to make sure it faithfully mirrors the concepts of the domain
2. There should be a way to automatically generate a textual description of the ubiquitous language starting from the code, this way it is guaranteed to always be in sync with the code

As described above (TODO: link al paragrafo precedente se possibile) the first problem was addressed by keeping the modelling of the domain concepts as easy as possible.

As for the second problem, we developed an sbt plugin [5] that, by parsing the documentation generated by unidoc, automatically generates markdown tables with the desired elements of the ubiquitous language.

The plugin itself was developed following the same attention to code quality, with automatic release, conventional commit standards and functional approach that we used for the development of the main project.

## Links
[1] https://blog.ploeh.dk/2017/01/27/from-dependency-injection-to-dependency-rejection/

[2] FIXME trova un link utile con spiegazione minimale

[3] https://www.youtube.com/watch?v=2JB1_e5wZmU

[4] https://pragprog.com/titles/swdddf/domain-modeling-made-functional/

[5] TODO LINK AL PLUGIN