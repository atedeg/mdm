---
title: Architecture
layout: static-site-main
---

# Architecture

The architecture of each bounded context follows the
[Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)'s structure: Entities, Use Cases, Interface Adapters, Frameworks and Drivers.  

- The first layer consists of the domain Aggregates we named `Types`; these are the domain entities
  and are the least likely to change when something external changes
- The second layer is composed of the domain "Actions", namely the business processes to model and
  "Domain Events": the starting point for almost all the business processes. They orchestrate
  the flow of data to and from the entities of the layer below
- For the _Interface Adapters layer_, in order to protect the layers underneath, we built
  Data Transfer Objects (DTOs) for every element that has to be transferred towards other bounded
  contexts or be persisted in a storage. We also used the Repository pattern to abstract over the
  particular data persistence infrastructure.
  DTOs were also used in the Anti-Corruption Layer and Open-Host Service patterns to convert and
  present external data to the Entities and Use Case layers
- As for the _Frameworks and Drivers_ layer, it contains the minimal amount of code needed to glue
  together the "communication code" (HTTP servers, Database persistence, etc.) with the code below.
  We implemented the HTTP servers belonging to this layer and mocked the remaining code related to
  data persistence or message-oriented communication

<img id="clean-arch" alt="Clean Architecture" src="#"/>
