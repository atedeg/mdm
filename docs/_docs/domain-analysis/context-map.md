---
title: Context Map
layout: static-site-main
---

# Context Map

- `MilkPlanning [D, ACL] <- [U] ClientOrders` and `ProductionPlanning [D, ACL] <- [U] ClientOrders`  
  `ClientOrders` sends a message to `MilkPlanning` and `ProductionPlanning` to inform a new order was received.
  `MilkPlanning` and `ProductionPlanning` are downstream *core domains* so it's necessary to insert an *Anti-Corruption Layer* between them and `ClientOrders`.
  Moreover, `ClientOrders` is going to be a *generic* bounded context, as reported in the Core Domain Chart.
- `MilkPlanning [D, ACL]  <- [U] Restocking`  
  `MilkPlanning` asks to `Restocking` the remaining quantity of milk and informs `Restocking` to place an order for the required amount of milk.
  In addition it asks to `Restocking` the quantity of milk used in the previous year. 
  `MilkPlanning` is a downstream core domain since `Restocking` provides a service to it and the latter is going to be a generic bounded context, as 
  reported in the Core Domain Chart. For all these reasons `MilkPlanning` has an Anti-Corruption Layer on its side.
- `Production [D, CF] <- [U] ProductionPlanning`  
  `ProductionPlanning` provides `Production` with the production plan for the day.
  As `ProductionPlanning` is the service provider concerning `Production`, they are respectively upstream and downstream.
  Since `ProductionPlanning` and `Production` are tightly coupled, the latter is *Conformist*.
- `Stocking [D, ACL] <- [U] ClientOrders`  
  `Stocking` receives a message from `ClientOrders` notifying the removal from stock of certain products.
  `Stocking` has an *Anti-Corruption Layer*, as `ClientOrders` is going to be a generic bounded context and it will be
  impossible to control the format of the messages.
- `ProductionPlanning [D, ACL] <- [U] Stocking`  
  `ProductionPlannig` asks `Stocking` for the number of products missing from the stock.
  Since `ProductionPlannig` is a downstream core bounded context, an Anti-Corruption Layer is required.
- `Stocking [D, CF] <- [U] Production`  
  `Production` informs `Stocking` that a batch is ripening.
  Since `Production` and `Stocking` are tightly coupled, the latter is Conformist.
- `Restocking [D] <- [U, CF] Production`  
  `Production` informs `Restocking` when some raw materials are consumed.
  `Production` is an upstream Open-Host Service and must expose a published language as the `Restocking` downstream bounded context
  is going to be generic and we will not be able to freely change its API.
- `MilkPlanning [D, ACL] <- [U] Stocking`  
  `MilkPlanning` asks `Stocking` for the amount of products in stock.
  Since `MilkPlanning` is a downstream core bounded context, and Anti-Corruption Layer is required.

There is a *Shared Kernel* among the bounded contexts which contains the definitions for **product** and **cheese type**.
This choice was taken as the two aforementioned concepts are crucial for the cheese factory and a change in any of the definitions must be reflected in all
bounded contexts handling these concepts.
In fact, adding a new kind of product involves a series of important domain changes that must be reflected in the code of different bounded contexts:
the production, ordering, labeling and stocking processes would need a rehaul to take into account the new kind of product.
By sharing this information among different bounded contexts it is guaranteed that, whenever a change happens to any of these concepts, all the domains
will maintain an up-to-date vision of these concepts.

![Context Map](images/contextMap.svg)
