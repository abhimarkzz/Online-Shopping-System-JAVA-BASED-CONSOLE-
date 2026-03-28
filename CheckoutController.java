package com.shopping.controllers;

import com.shopping.Main;
import com.shopping.models.CartItem;
import com.shopping.models.Customer;
import com.shopping.models.Order;
import com.shopping.models.UserSession;
import com.shopping.services.CartService;
import com.shopping.services.OrderService;
import com.shopping.services.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CheckoutController implements Initializable {

    @FXML private TextField shipNameField;
    @FXML private TextField shipEmailField;
    @FXML private TextField shipAddressField;
    @FXML private TextField shipCityField;
    @FXML private TextField shipZipField;
    @FXML private TextField cardField;
    @FXML private TextField cvvField;
    @FXML private RadioButton payCard;
    @FXML private RadioButton payUpi;
    @FXML private RadioButton payCod;
    @FXML private ListView<String> orderItemsList;
    @FXML private Label checkoutSubtotal;
    @FXML private Label checkoutTotal;
    @FXML private Label checkoutError;

    private final CartService  cartService  = ServiceLocator.getInstance().getCartService();
    private final OrderService orderService = ServiceLocator.getInstance().getOrderService();
    private final UserSession  session      = UserSession.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Pre-fill from customer profile
            Customer c = session.getCurrentCustomer();
            shipNameField.setText(c.getFullName());
            shipEmailField.setText(c.getEmail());
            if (c.getAddress() != null && !c.getAddress().isBlank()) {
                shipAddressField.setText(c.getAddress());
            }

            // Populate item list
            List<CartItem> items = cartService.getCartItems();
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (CartItem item : items) {
                lines.add(String.format("%-30s x%d   $%.2f",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getSubtotal()));
            }
            orderItemsList.setItems(FXCollections.observableArrayList(lines));

            String total = String.format("$%.2f", cartService.getTotal());
            checkoutSubtotal.setText(total);
            checkoutTotal.setText(total);

            // Group the radio buttons
            ToggleGroup tg = new ToggleGroup();
            payCard.setToggleGroup(tg);
            payUpi.setToggleGroup(tg);
            payCod.setToggleGroup(tg);
            payCard.setSelected(true);

        } catch (Exception e) {
            System.err.println("[CheckoutController] initialize: " + e.getMessage());
        }
    }

    @FXML
    private void handlePlaceOrder() {
        try {
            // Validate fields
            if (shipNameField.getText().isBlank()) { setError("Please enter your full name."); return; }
            if (shipEmailField.getText().isBlank()) { setError("Please enter your email."); return; }
            if (shipAddressField.getText().isBlank()) { setError("Please enter your address."); return; }
            if (shipCityField.getText().isBlank()) { setError("Please enter your city."); return; }

            String address = shipAddressField.getText().trim() + ", "
                    + shipCityField.getText().trim() + " "
                    + shipZipField.getText().trim();

            List<CartItem> items = cartService.getCartItems();
            if (items.isEmpty()) { setError("Your cart is empty!"); return; }

            Customer customer = session.getCurrentCustomer();
            Order order = orderService.placeOrder(customer, items, address);

            if (order == null) { setError("Failed to place order. Please try again."); return; }

            cartService.clearCart();

            // Show success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Placed!");
            alert.setHeaderText("🎉  Order Confirmed");
            alert.setContentText("Your order " + order.getOrderId()
                    + " has been placed successfully!\nTotal: $"
                    + String.format("%.2f", order.getTotalAmount())
                    + "\nYou can track it in My Orders.");
            alert.showAndWait();

            Main.switchScene("orders.fxml");

        } catch (Exception e) {
            setError("Order error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleBackToCart() { Main.switchScene("cart.fxml"); }

    private void setError(String msg) {
        if (checkoutError != null) {
            checkoutError.setText(msg);
            checkoutError.setStyle("-fx-text-fill:#e94560;");
        }
    }
}