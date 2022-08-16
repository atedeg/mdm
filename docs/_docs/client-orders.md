---
title: Client Orders
---

# Client Orders

The system receives all incoming orders from the clients; each incoming order
line specifies a product and the required quantity. Moreover, the incoming order
contains information about the client, the expected delivery date and the
delivery location.
Finally, each order is identified by a unique ID in order to allow order tracking.

After an order is received it has to be priced: a priced order has the same structure
of an incoming order but each order line also specifies the total price.
It is computed by multiplying the ordered quantity and the product's price (which is taken
from a price list).
The priced order also specifies the
total price which is obtained from the sum of the lines' prices.

After the order is priced it considered in progress, and it is the operator's job
to palletize the products needed for each order.
The order is considered incomplete until all the required products are palletized.

When an order is completed by the operator (i.e. all the products are taken from the
stock and palletized) it is weighted; then, the operator prints the order's transport
document and attach it to the pallet which is loaded onto a truck. A notification is sent
to the client notifying them the order has been shipped.

## Ubiquitous Language

{% include client-orders-ul.md %}

## Domain Events

### Incoming Events

{% include client-orders-incoming.md %}

### Outgoing Events

{% include client-orders-outgoing.md %}
