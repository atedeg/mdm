---
title: Event Storming
---
# Motivations
Given the complexity of the domain, it was decided to use Event Storming to become familiar with
the terminologies and workflows typical of the domain. The adoption of this activity is justified by having
real domain experts available to explain the current business processes to us. In addition, using this approach it was
possible to bring together managers from different areas, who collaborated to correctly define the dynamics and problems
concerning their communications.

# Subdomains
From the Event Storming session the following subdomains emerged:
- **Production planning**: creates the daily production plan
- **Milk planning**: estimates the amount of milk to order each week
- **Stocking**: manages the packaging of cheeses and their stocking
- **Restocking**: orders the milk and keeps track of the available quantity of milk
- **Client orders**: handles and fulfills the orders made by clients
- **Production**: tracks the cheeses' production process

We then proceeded to have a more in-depth analysis of each subdomain in order to determine their business differentiation and to do a first estimate of the model's complexity.
The following considerations were made:
- **production planning** and **milk planning** are to be considered *core subdomains*, as them being as precise and accurate as possible can be decisive in order to optimize the production
- the **milk planning** subdomain can be seen as a *big-bet subdomain*, since it has the potential to significantly disrupt the market if implemented in such a way that can predict trends and spikes in orders
- the remaining subdomains introduce a lower amount of business differentiation, so they are to be considered as *supporting subdomains*

We gathered these pieces of information and compiled the following core domain chart:
![Core domain chart](images/core-domain-chart.png)
