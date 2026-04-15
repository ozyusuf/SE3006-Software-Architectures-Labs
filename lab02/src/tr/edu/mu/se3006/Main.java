package tr.edu.mu.se3006;
import tr.edu.mu.se3006.catalog.CatalogService;
import tr.edu.mu.se3006.catalog.CatalogFactory;
import tr.edu.mu.se3006.orders.OrderController;
import tr.edu.mu.se3006.orders.OrdersFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 System Starting in Modular Monolith Mode...");
        System.out.println("----------------------------------------------\n");
        
        // TODO 1: Create the Catalog Module via its Factory
        CatalogService catalog = CatalogFactory.create();

        // TODO 2: Create the Orders Module via its Factory, passing the catalog module
        OrderController controller = OrdersFactory.create(catalog);

        System.out.println("--- Test Scenarios ---");
        controller.handleUserRequest(1L, 2);   // Success (MacBook Pro, stock=5)
        controller.handleUserRequest(2L, 25);  // Fail (Logitech Mouse, stock=20, insufficient)
        controller.handleUserRequest(3L, 1);   // Fail (product not found)
    }
}
