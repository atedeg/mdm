---
title: Production
layout: static-site-main
---

# Production
Every day the dairyman receives a production plan from Raffaella, containing instructions
on the cheese he needs to produce for the day.

To determine the ingredients to use for a production the dairyman uses a
recipe book where, for each type of cheese, there is a recipe.
A recipe specifies the quintals of each ingredient needed to produce a quintal of the given type of
cheese.
> _e.g._ The recipe for a quintal of ricotta requires 1.5 quintals of milk,
> a tenth of quintal of rennet and a tenth of quintal of salt

Once the appropriate recipe is chosen the production can start by retrieving the
needed ingredients.

Once an in-progress production ends (the smart machines will send an appropriate message
to signal the end of the process), the produced cheese is assigned a batch ID
and stored in a refrigeration room.

## Ubiquitous Language

{% include production-ul.md %}

## Domain Events

### Incoming Events

{% include production-incoming.md %}

### Outgoing Events

{% include production-outgoing.md %}
