package tr.edu.mu.se3006.orders;
import tr.edu.mu.se3006.catalog.CatalogService;

// PUBLIC Factory: Wires the orders module. Needs Catalog API to function.
public class OrdersFactory {
    public static OrderController create(CatalogService catalogService) {
        OrderRepository orderRepository = new OrderRepository();
        OrderService orderService = new OrderService(catalogService, orderRepository);
        return new OrderController(orderService);
    }
}
