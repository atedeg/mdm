---
title: Architecture
layout: static-site-main
---

# Architecture

The architecture of each bounded context follows the Clean Architecture's structure: Entities, Use Cases, Interface Adapters, 
Frameworks and Drivers.  
The first layer consists of the domain Aggregates we named "Types", which are all the unmodifiable domain entities and 
their invariants.  
The second layer is composed of the "Actions", namely the business processes to model, and "Domain Events", 
the starting points for almost all the business processes.  
For the Interface Adapters layer, in order to protect the layers underneath, we built Data Transfer Objects (DTOs) 
for every element that have to be transferred 
towards other bounded contexts, and we used the Repository pattern to abstract over the data persistence infrastructure.  
DTOs were also used in the Anti-Corruption Layer and Open-Host Service patterns to convert and present external 
data to the Entities and Use Case layers.  
Finally, although we set up the APIs for some bounded context, the Drivers and Frameworks layer is completely mocked
so databases and message-oriented communications were not implemented.

<img id="clean-arch" alt="Clean Architecture" src="#"/>
