---
title: Pricing
---

# Pricing
Every time an order is received, each order line must be priced accordingly.
Each product has a base unit price coming from a price list.
If the client is a regular customer, the price for a given order line is simply computed as the base price for
that product multiplied by the ordered quantity.

However, some clients have made special arrangements with Raffaella, and they have an associated promotion.
A promotion is referred to a single client, and has an expiry date, after which the promotion is no longer valid.
A client can have more than one active promotion at any time.
A promotion can be of two distinct types:
* in the fixed promotion, one or more products are discounted by a fixed percentage (e.g. each 100g casatella is 5% off);
* in the threshold promotion, given one or more products and their respective quantity thresholds, the exceeding quantity for each product is discounted by a fixed percentage (e.g. after the 5th, each 100g casatella is 5% off).

## Ubiquitous Language

{% include pricing-ul.md %}
