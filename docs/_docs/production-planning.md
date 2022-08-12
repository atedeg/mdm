---
title: Production Planning
---

# Production Planning

Every morning Raffaella has to create a production plan which contains how many of each product to produce that day.

She makes this plan taking into account the following factors:
- the production plan for the same day of the previous year
- the products that have to be manufactured that day to fulfil new orders considering the ripening time of each cheese type.
  > _e.g._  a caciotta takes seven days to ripen, so the production has to start seven days before the order's deadline.
  
  If an order contains a product with ripening days that takes more time than the order required date, it will be delayed.
- the products needed to replenish the stock

> 💡 The completed production plan is sent to the production B.C.

## Ubiquitous Language

{% include production-planning-ul.md %}

## Domain Events

### Incoming Events

{% include production-planning-incoming.md %}

### Outgoing Events

{% include production-planning-outgoing.md %}
