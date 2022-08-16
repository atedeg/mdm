---
title: "User stories"
---

# User stories

During our event storming session we discussed with the domain experts and Raffaella (the factory owner).
After sketching out the main subdomains, we came up with this set of user stories:

## Milk Planning
1. **As** Raffaella  
   **I want to** estimate the milk needed to sustain the following week’s production  
   **so that** I can order the exact quantity of milk minimizing waste

## Production Planning
1. **As** Raffaella  
   **I want to** create a production plan for the day  
   **so that** I can optimize the usage of the cheese making machines during the day

## Client Orders
1. **As** a customer  
   **I want to** place an order   
   **so that** I can buy cheese
2. **As** an operator  
   **I want** the system to automatically price incoming orders  
   **so that** I don't have to do it manually flipping through the price list
3. **As** an operator  
   **I want to** palletize a product for an order  
   **so that** I can set aside a produce for a client's order
4. **As** an operator  
   **I want to** mark an order as complete and ready to be shipped   
   **so that** it can be shipped to the customer
5. **As** an operator  
   **I want to** automatically generate a transport document for an order ready to be shipped  
   **so that** I don't have to do it manually

## Pricing
1. **As** Raffaella
   **I want to** calculate the price of a certain product (with its quantity) for a given client
   **so that** I can build customer loyalty by making discounts to particular clients

## Restocking
1. **As** Raffaella  
   **I want to** place orders for raw materials  
   **so that** I won't run out of crucial ingredients for the cheese-making process

## Production
1. **As** a dairyman  
   **I want to** choose a recipe to start the production process  
   **so that** I can start the production process  
2. **As** a dairyman  
   **I want to** know when the cheeses are ready to go in the refrigerating room  
   **so that** I can put them in the refrigeration room  

## Stocking
1. **As** a worker  
   **I want to** know which cheeses are ready to be wrapped  
   **so that** I can wrap them and put them in the refrigeration room
2. **As** a worker  
   **I want to** report the result of quality assurance on a batch of cheeses  
   **so that** I can know which cheeses to pull off from the market
3. **As** a worker  
   **I want to** I want to weigh a cheese    
   **so that** I can print an appropriate label for it
