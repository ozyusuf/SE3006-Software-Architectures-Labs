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

### 3. Discussion: Layered Architecture vs. Modular Monolith

#### How They Differ

The two labs implement the same business logic but organize it in fundamentally different ways.

In **Layered Architecture** (Lab 01), code is organized **horizontally** by technical concern. Every class in the system lives in a layer defined by what it does: `presentation`, `business`, or `persistence`. The `OrderService` in the business layer directly imports and uses `ProductRepository` from the persistence layer. There are no walls between business domains — an `OrderService` can freely reach into any repository it wants. All classes are `public`.

In **Modular Monolith** (Lab 02), code is organized **vertically** by business domain. The `catalog` and `orders` packages are self-contained modules. Each module owns its own data, logic, and persistence internally. The `orders` module cannot access `ProductRepository` or `Product` at all — not because of a coding convention, but because the Java compiler enforces it through `package-private` visibility. Cross-module communication happens exclusively through a public interface (`CatalogService`).

| Aspect | Layered Architecture | Modular Monolith |
|---|---|---|
| Organization | Horizontal layers (by technical role) | Vertical modules (by business domain) |
| Coupling | Cross-layer access is open by default | Enforced boundaries via `package-private` |
| Data sharing | Any service can access any repository | A module's data is hidden behind an interface |
| Dependency direction | Service → Repository (direct) | Module → Interface (indirect) |
| Access control | All classes `public` | Only interfaces and factories are `public` |

#### Pros and Cons

**Layered Architecture**

Pros:
- Simple and easy to understand for small systems. The mental model (presentation calls business, business calls persistence) is intuitive and widely known.
- Very fast to get started — no need to design module boundaries upfront.
- Works well when the application has a single, unified domain and a small team.

Cons:
- As the system grows, layers become dumping grounds. The business layer ends up with every service for every domain mixed together.
- Nothing prevents a developer from bypassing layers — an `OrderService` can accidentally start calling a `UserRepository`, creating hidden dependencies.
- Changes to one part of the business logic can have unexpected ripple effects across the entire layer.
- Harder to split into independent teams or services later, because domain logic is not separated.

**Modular Monolith**

Pros:
- Strong boundaries are enforced by the language itself, not just conventions. A developer physically cannot access another module's internals.
- Each module can evolve independently. The `catalog` module's internal implementation can be completely rewritten without touching the `orders` module.
- Business domains are explicit and clearly visible in the package structure. It is easier to understand what the system does by looking at the module names.
- A natural stepping stone to microservices: if the system needs to scale, each module is already a self-contained unit that can be extracted.

Cons:
- Requires more upfront design effort to define module boundaries correctly. A wrong boundary is expensive to refactor.
- More boilerplate: every module needs a public interface, a factory, and careful visibility management.
- Cross-module queries (e.g., listing orders with product names) require coordinating through interfaces, which can be more complex than a simple join across repositories in the layered approach.
- Can feel over-engineered for very small or short-lived projects.

#### When to Choose Which

A Layered Architecture is appropriate for small applications, prototypes, or systems where the entire business domain is managed by one team and is unlikely to grow significantly in scope.

A Modular Monolith is the better choice when the system is expected to grow, when multiple teams will work on different parts of the codebase, or when there is a possibility of eventually splitting the system into independent services. The discipline of defining clear boundaries early pays off as the codebase scales.

---

### 4. Conclusion
In this lab, I learned how a **Modular Monolith** improves on a Layered Architecture by enforcing stronger boundaries between business domains. Using Java's `package-private` access modifier, the internal implementation details of each module (repository, model, service implementation) are completely hidden from the outside. Other modules can only interact through the public `CatalogService` interface, which means the `catalog` module could completely change its internal implementation without affecting the `orders` module at all. The Factory pattern was the mechanism that made this possible: it assembles the internal components and hands out only the interface. This is a much more maintainable and decoupled design than the one in Lab 01.
