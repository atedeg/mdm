---
title: Milk Planning
layout: static-site-main
---

# Milk Planning

Every Saturday Raffaella has to estimate the quintals of milk necessary to produce all products
for the following week.
She makes this estimate by taking into account the following factors:

- the quintals of milk processed in the same period of the previous year
- the quintals of milk needed by the products that have to be produced in the following week
  (this is made by reading from a recipe book the yield of milk to produce a quintal of a given product)
- the current stock
- the quintals of milk already in stock

> 💡 After the estimate is complete the restocking B.C. is notified of the result so
> that it can make a milk order

## Ubiquitous Language

{% include milk-planning-ul.md %}

## Domain Events

### Incoming Events

{% include milk-planning-incoming.md %}

### Outgoing Events

{% include milk-planning-outgoing.md %}
