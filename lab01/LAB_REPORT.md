# SE 3006: Software Architecture - Lab 01 Report
## Topic: Layered Architecture Built with Pure Java

**Student Name:** Yusuf Öz
**Date:** 2026-04-08

### 1. Goal of the Experiment
The goal of this lab was to implement a strict layered architecture in Java without using any frameworks like Spring Boot. The main focus was to see how Manual Dependency Injection works and how layers communicate with each other strictly from top to bottom.

### 2. Implementation Steps

#### Persistence Layer (`ProductRepository.java`)
- Created a `HashMap<Long, Product>` to act as an in-memory database.
- Implemented `findById(Long id)` to get products from the map and `save(Product product)` to insert or update them.

#### Business Layer (`OrderService.java`)
- Used Constructor Injection to inject the `ProductRepository` dependency into the service.
- Implemented the `placeOrder(Long productId, int quantity)` method. It finds the product, checks if the stock is enough, throws an `IllegalArgumentException` if there isn't enough stock, and updates the stock before saving it back to the repository.

#### Presentation Layer (`OrderController.java`)
- Used Constructor Injection to inject the `OrderService`.
- Implemented the `handleUserRequest` method using a `try-catch` block. It calls `placeOrder` and prints a success message if it works, or an error message if an exception is thrown in the business layer.

#### System Bootstrap (`Main.java`)
- Created the objects from bottom to top: first `ProductRepository`, then `OrderService`, and finally `OrderController`.
- Tested the system by calling `handleUserRequest` with both successful and failing scenarios (like not enough stock or non-existent product).

### 3. Conclusion
In this lab, I learned how to build a strict layered architecture manually. By separating the presentation, business logic, and data access, the code becomes more organized and decoupled. I also understood the importance of dependency injection and how it helps higher-level layers interact with lower-level layers without tight coupling.
