# SE 3006: Software Architecture - Lab 02 Report
## Topic: Modular Monolith Built with Pure Java

**Student Name:** Yusuf Öz
**Date:** 2026-04-15

### 1. Goal of the Experiment
The goal of this lab was to refactor the tightly coupled Layered Architecture from Lab 01 into a **Modular Monolith**. Instead of organizing code in horizontal layers (presentation, business, persistence), the system is now split into vertical business modules (`catalog` and `orders`). The main focus was to understand **Information Hiding** using Java's `package-private` access modifier, enforce **modular boundaries** through public interfaces, and use the **Factory pattern** to wire internal dependencies without exposing them.

### 2. Implementation Steps

#### Catalog Module Internal Logic (`ProductRepository.java`, `CatalogServiceImpl.java`)
- Implemented `findById(Long id)` and `save(Product product)` in `ProductRepository` using a `HashMap` — the same in-memory database approach as Lab 01, but now `package-private` so no other module can access it directly.
- Added a `ProductRepository` field to `CatalogServiceImpl` and used Constructor Injection to receive it.
- Implemented `checkAndReduceStock`: finds the product, throws `IllegalArgumentException` if not found or if stock is insufficient, reduces the stock, and saves the updated product back to the repository.

#### Catalog Module Factory (`CatalogFactory.java`)
- Implemented `CatalogFactory.create()` to instantiate a `ProductRepository`, pass it to `CatalogServiceImpl`, and return the result as the `CatalogService` interface.
- This is the key to information hiding: the caller gets back only the interface, with no knowledge of `ProductRepository` or `CatalogServiceImpl`.

#### Orders Module Logic (`OrderService.java`, `OrderController.java`)
- `OrderService` takes `CatalogService` and `OrderRepository` via Constructor Injection. The `placeOrder` method delegates stock checking entirely to `catalogService.checkAndReduceStock()` — it never touches a `Product` object directly.
- `OrderController` wraps `placeOrder` in a `try-catch` block, printing a success message or an error message depending on the outcome, just like in Lab 01.

#### Orders Module Factory (`OrdersFactory.java`)
- `OrdersFactory.create(CatalogService catalogService)` wires together all order module components and returns the `OrderController`. The `CatalogService` is received as a parameter, enforcing that the orders module depends only on the catalog's public interface.

#### System Bootstrap (`Main.java`)
- Used `CatalogFactory.create()` to create the catalog module, then passed it to `OrdersFactory.create()` to create the orders module.
- Tested with three scenarios: a successful order, an order with insufficient stock, and an order for a non-existent product.

### 3. Conclusion
In this lab, I learned how a **Modular Monolith** improves on a Layered Architecture by enforcing stronger boundaries between business domains. Using Java's `package-private` access modifier, the internal implementation details of each module (repository, model, service implementation) are completely hidden from the outside. Other modules can only interact through the public `CatalogService` interface, which means the `catalog` module could completely change its internal implementation without affecting the `orders` module at all. The Factory pattern was the mechanism that made this possible: it assembles the internal components and hands out only the interface. This is a much more maintainable and decoupled design than the one in Lab 01.
