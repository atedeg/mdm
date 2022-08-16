---
title: Restocking
layout: static-site-main
---

# Restocking

The Restocking bounded context is responsible to fulfill the requests to order
milk coming from the Milk Planning.
Moreover, it keeps track of the quintals of stocked milk that could be used by
other bounded contexts.
Lastly, this bounded context also keeps track of other ingredients (e.g. rennet,
salt, probiotics, etc.) and their consumption when a production is started.

## Ubiquitous Language

{% include restocking-ul.md %}

## Domain Events

### Incoming Events

{% include restocking-incoming.md %}
