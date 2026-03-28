package com.shopping.services;

import com.shopping.datastructures.CustomLinkedList;
import com.shopping.models.*;

import java.util.*;

/**
 * Manages orders.
 * Pending orders → CustomQueue (FIFO processing).
 * All orders history → CustomLinkedList.
 * Persisted via CSV.
 */
public class OrderService {

    private CustomLinkedList<Order> allOrders;
    private CustomQueue<Order> pendingQueue;
    private final ProductService productService;
    private int orderCounter = 1000;

    public OrderService(ProductService productService) {
        this.productService = productService;
        allOrders   = new CustomLinkedList<>();
        pendingQueue = new CustomQueue<>();
    }

    // ── Bootstrap ─────────────────────────────────────────────────────────────

    public void loadFromCsv(CustomerService customerService) {
        try {
            List<String[]> rows = FileManager.loadOrdersRaw();
            for (String[] row : rows) {
                if (row.length < 9) continue;
                Order o = new Order();
                try {
                    o.setOrderId(row[0].trim());
                    o.setCustomerId(Integer.parseInt(row[1].trim()));
                    o.setCustomerName(row[2].trim());
                    o.setCustomerEmail(row[3].trim());
                    o.setShippingAddress(row[4].trim());
                    // row[5] = items csv string — rebuild CartItems from product ids
                    CustomLinkedList<CartItem> items = rebuildItems(row[5].trim());
                    o.setItems(items);
                    o.setTotalAmount(Double.parseDouble(row[6].trim()));
                    o.setStatus(Order.Status.valueOf(row[7].trim()));
                    o.setOrderDate(row[8].trim());
                    // Parse order number for counter
                    String numPart = o.getOrderId().replaceAll("[^0-9]", "");
                    if (!numPart.isEmpty()) {
                        int num = Integer.parseInt(numPart);
                        if (num >= orderCounter) orderCounter = num + 1;
                    }
                    allOrders.addLast(o);
                    if (o.getStatus() == Order.Status.PENDING) pendingQueue.enqueue(o);
                } catch (Exception inner) {
                    System.err.println("[OrderService] Skip malformed order row: " + inner.getMessage());
                }
            }
            System.out.println("[OrderService] Loaded " + allOrders.size() + " orders.");
        } catch (Exception e) {
            System.err.println("[OrderService] loadFromCsv error: " + e.getMessage());
        }
    }

    private CustomLinkedList<CartItem> rebuildItems(String itemsCsv) {
        CustomLinkedList<CartItem> list = new CustomLinkedList<>();
        try {
            if (itemsCsv == null || itemsCsv.isBlank()) return list;
            for (String part : itemsCsv.split("\\|")) {
                String[] kv = part.split(":");
                if (kv.length < 2) continue;
                int productId = Integer.parseInt(kv[0].trim());
                int qty       = Integer.parseInt(kv[1].trim());
                Product p = productService.getById(productId);
                if (p != null) list.addLast(new CartItem(p, qty));
            }
        } catch (Exception e) {
            System.err.println("[OrderService] rebuildItems error: " + e.getMessage());
        }
        return list;
    }

    // ── Place Order ───────────────────────────────────────────────────────────

    public Order placeOrder(Customer customer, List<CartItem> cartItems,
                            String shippingAddress) {
        try {
            if (cartItems == null || cartItems.isEmpty()) return null;

            String orderId = "ORD-" + (orderCounter++);
            CustomLinkedList<CartItem> items = CustomLinkedList.fromList(cartItems);
            double total = cartItems.stream()
                    .mapToDouble(CartItem::getSubtotal).sum();

            Order order = new Order(orderId, customer.getId(), customer.getFullName(),
                    customer.getEmail(), shippingAddress, items, total);

            // Deduct stock
            for (CartItem item : cartItems) {
                productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
            }

            allOrders.addLast(order);
            pendingQueue.enqueue(order);
            FileManager.saveOrder(order);

            System.out.println("[OrderService] Order placed: " + orderId);
            return order;
        } catch (Exception e) {
            System.err.println("[OrderService] placeOrder error: " + e.getMessage());
            return null;
        }
    }

    // ── Queue Processing (Admin) ───────────────────────────────────────────────

    public Order processNextOrder() {
        try {
            if (pendingQueue.isEmpty()) return null;
            Order o = pendingQueue.dequeue();
            o.setStatus(Order.Status.PROCESSING);
            saveAll();
            return o;
        } catch (Exception e) {
            System.err.println("[OrderService] processNextOrder error: " + e.getMessage());
            return null;
        }
    }

    public boolean updateStatus(String orderId, Order.Status newStatus) {
        try {
            for (Order o : allOrders) {
                if (o.getOrderId().equals(orderId)) {
                    o.setStatus(newStatus);
                    saveAll();
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("[OrderService] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Order> getAllOrders() {
        try { return allOrders.toList(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        try {
            for (Order o : allOrders) {
                if (o.getCustomerId() == customerId) result.add(o);
            }
        } catch (Exception e) {
            System.err.println("[OrderService] getOrdersByCustomer error: " + e.getMessage());
        }
        return result;
    }

    public Order getById(String orderId) {
        try {
            for (Order o : allOrders) {
                if (o.getOrderId().equals(orderId)) return o;
            }
        } catch (Exception e) {
            System.err.println("[OrderService] getById error: " + e.getMessage());
        }
        return null;
    }

    public int getPendingCount() { return pendingQueue.size(); }
    public int getTotalCount()   { return allOrders.size(); }

    // ── Persistence ───────────────────────────────────────────────────────────

    private void saveAll() {
        try {
            FileManager.saveAllOrders(allOrders.toList());
        } catch (Exception e) {
            System.err.println("[OrderService] saveAll error: " + e.getMessage());
        }
    }
}