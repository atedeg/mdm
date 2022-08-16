---
title: Event Storming
layout: static-site-main
---

# Event Storming

## Motivations

Given the complexity of the domain, it was decided to use [Event Storming](https://www.eventstorming.com)
to become familiar with the terminologies and workflows typical of the domain.
The adoption of this activity was also justified by having real domain experts available to explain us the
current business processes.
In addition, using this approach it was possible to bring together managers from different areas,
who collaborated to correctly define the dynamics and problems concerning their communications.

## The Process

We first held an Event Storming session with three domain experts working in the Mambelli cheese factory:
Raffaella, the owner and responsible for the planning; Gianluca, the responsible for the correct
handling of client orders; and Mattia, a cheese maker, responsible for the production of the cheese.

All the experts were kind enough to give us an hour of their time and gladly shared their knowledge and
experience. The whole process (that can be seen in a sped-up version in the video below) lasted a little
less than an hour.

![Event Storming Video](images/eventStormingVideo.gif)

We found Event Storming to be a refreshing way to approach domain modeling;
the experts were immediately engaged by the playfulness of the process and time flew by as we deepened
our knowledge of the domain. It also was a great way to understand how the different
domain experts interacted with each other and what were the pain points they wish they could improve.

![Event Storming](images/eventStorming.svg)

After this first session, we kept in contact with Raffaella and Gianluca who helped us during the first
phases of development when we had to define more precisely the ubiquitous language of the different
bounded contexts.

## Subdomains

These are the subdomains that emerged from the Event Storming process:

- **Production Planning**: creates the daily production plan
- **Milk Planning**: estimates the amount of milk to order each week
- **Stocking**: manages the packaging of cheeses and their stocking
- **Restocking**: orders the milk and keeps track of the available quantity of milk
- **Client Orders**: handles and fulfills the orders made by clients
- **Production**: tracks the cheeses' production process

We then proceeded to have a more in-depth analysis of each subdomain to determine their business
differentiation and to do a first estimate of the model's complexity.
The following considerations were made:

- **Production Planning** and **Milk Planning** are to be considered *core subdomains*
  since they can be decisive in order to optimize the efficiency of the factory
- the **Milk Planning** subdomain can be seen as a *big-bet subdomain* since it has the potential
  to significantly disrupt the market if implemented in such a way that can predict trends and
  spikes in orders
- the remaining subdomains introduce a lower amount of business differentiation, so they are to be
  considered as *supporting subdomains*

We gathered these pieces of information and compiled the following core domain chart:
![Core domain chart](images/core-domain-chart.png)

As for the **Client Orders** and **Restocking** subdomains, they are considered to be *supporting*
for the moment since it would be unfeasible to buy a full-fledged SAP-like system.
For now, it may suffice to implement a custom-made minimal solution to cut costs.
In the future, with an increased sales volume and revenue, these domains will become generic by
relying on a third-party solution.
