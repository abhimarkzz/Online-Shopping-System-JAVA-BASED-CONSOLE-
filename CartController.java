package com.shopping.controllers;

import com.shopping.models.CartItem;
import com.shopping.services.CartService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CartController {

    @FXML private Label itemCountLabel;
    @FXML private VBox cartItemsBox;
    @FXML private Label totalLabel;
    @FXML private Label emptyLabel;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private Button checkoutButton;
    @FXML private Button backButton;

    private CartService cartService;

    @FXML
    public void initialize() {
        try {
            cartService = CartService.getInstance();
            refreshCart();
        } catch (Exception e) {
            System.err.println("CartController init error: " + e.getMessage());
        }
    }

    private void refreshCart() {
        try {
            cartItemsBox.getChildren().clear();
            int count = cartService.getCartItemCount();
            itemCountLabel.setText("Items: " + count);

            if (count == 0) {
                emptyLabel.setVisible(true);
                checkoutButton.setDisable(true);
            } else {
                emptyLabel.setVisible(false);
                checkoutButton.setDisable(false);
                for (CartItem item : cartService.getCartItems()) {
                    cartItemsBox.getChildren().add(createCartRow(item));
                }
            }

            totalLabel.setText(String.format("Total: ₹%.2f", cartService.getCartTotal()));
            undoButton.setDisable(!cartService.canUndo());
            redoButton.setDisable(!cartService.canRedo());
        } catch (Exception e) {
            System.err.println("refreshCart error: " + e.getMessage());
        }
    }

    private HBox createCartRow(CartItem item) {
        HBox row = new HBox(12);
        row.getStyleClass().add("cart-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        VBox info = new VBox(4);
        Label name = new Label(item.getProduct().getName());
        name.getStyleClass().add("cart-item-name");
        Label cat = new Label(item.getProduct().getCategory());
        cat.getStyleClass().add("cart-item-cat");
        info.getChildren().addAll(name, cat);

        HBox.setHgrow(info, Priority.ALWAYS);

        // Quantity controls
        Button minus = new Button("−");
        minus.getStyleClass().add("qty-btn");
        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.getStyleClass().add("qty-label");
        Button plus = new Button("+");
        plus.getStyleClass().add("qty-btn");

        minus.setOnAction(e -> {
            try {
                if (item.getQuantity() > 1) {
                    cartService.updateQuantity(item.getProduct().getId(), item.getQuantity() - 1);
                } else {
                    cartService.removeFromCart(item.getProduct().getId());
                }
                refreshCart();
            } catch (Exception ex) { System.err.println(ex.getMessage()); }
        });

        plus.setOnAction(e -> {
            try {
                int maxStock = item.getProduct().getStock();
                if (item.getQuantity() < maxStock) {
                    cartService.updateQuantity(item.getProduct().getId(), item.getQuantity() + 1);
                    refreshCart();
                }
            } catch (Exception ex) { System.err.println(ex.getMessage()); }
        });

        HBox qtyBox = new HBox(8, minus, qtyLabel, plus);
        qtyBox.setAlignment(Pos.CENTER);

        Label price = new Label(String.format("₹%.2f", item.getTotalPrice()));
        price.getStyleClass().add("cart-item-price");

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("btn-remove");
        removeBtn.setOnAction(e -> {
            try {
                cartService.removeFromCart(item.getProduct().getId());
                refreshCart();
            } catch (Exception ex) { System.err.println(ex.getMessage()); }
        });

        row.getChildren().addAll(info, qtyBox, price, removeBtn);
        return row;
    }

    @FXML private void handleUndo() {
        try { cartService.undo(); refreshCart(); }
        catch (Exception e) { System.err.println("undo error: " + e.getMessage()); }
    }

    @FXML private void handleRedo() {
        try { cartService.redo(); refreshCart(); }
        catch (Exception e) { System.err.println("redo error: " + e.getMessage()); }
    }

    @FXML private void clearCart() {
        try {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Clear all items from cart?", ButtonType.YES, ButtonType.NO);
            a.setHeaderText(null);
            a.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    cartService.clearCart();
                    refreshCart();
                }
            });
        } catch (Exception e) { System.err.println("clearCart error: " + e.getMessage()); }
    }

    @FXML private void proceedToCheckout() { navigate("/fxml/cart.fxml"); }

    @FXML private void goBack() { navigate("/fxml/orders.fxml"); }

    private void navigate(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1100, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("navigate error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}