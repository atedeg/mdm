---
title: Milk Planning
---

# Milk Planning

Every Saturday Raffaella has to estimate the quintals of milk necessary to produce all products
for the following week.
She makes this estimate by taking into account the following factors:

- the quintals of milk processed in the same period of the previous year
- the quintals of milk needed by the incoming orders that have to be processed in the following week (this is made reading from a recipe book the the quintals of milk needed to produce a quintal of a given product)
- the current stock

> 💡 A domain event will be sent to the restocking B.C. so that it can make a milk order

## Ubiquitous Language

{% include milk-planning-ul.md %}

## Domain Events

{% include milk-planning-de.md %}
