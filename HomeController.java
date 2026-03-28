package com.shopping.controllers;

import com.shopping.models.Product;
import com.shopping.models.UserSession;
import com.shopping.services.CartService;
import com.shopping.services.CustomerService;
import com.shopping.services.ProductService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class HomeController {

    @FXML private Label welcomeLabel;
    private Label cartCountLabel; // not injected - cart count shown via cartButton text
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> sortCombo;
    @FXML private FlowPane productGrid;
    @FXML private Button cartButton;
    @FXML private Button adminButton;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;

    private ProductService productService;
    private CartService cartService;
    private CustomerService customerService;

    @FXML
    public void initialize() {
        try {
            productService = ProductService.getInstance();
            cartService    = CartService.getInstance();
            customerService = CustomerService.getInstance();

            UserSession session = UserSession.getInstance();
            welcomeLabel.setText("Welcome, " + session.getCurrentUser().getFirstName() + "!");

            // Show admin button only for admins
            adminButton.setVisible(session.isAdmin());
            adminButton.setManaged(session.isAdmin());

            setupCategoryCombo();
            setupSortCombo();
            loadProducts(productService.getAllProducts());
            updateCartCount();

            searchField.textProperty().addListener((obs, o, n) -> filterProducts());
        } catch (Exception e) {
            System.err.println("HomeController init error: " + e.getMessage());
        }
    }

    private void setupCategoryCombo() {
        try {
            categoryCombo.getItems().add("All Categories");
            categoryCombo.getItems().addAll(productService.getCategories());
            categoryCombo.setValue("All Categories");
            categoryCombo.setOnAction(e -> filterProducts());
        } catch (Exception e) {
            System.err.println("setupCategoryCombo error: " + e.getMessage());
        }
    }

    private void setupSortCombo() {
        try {
            sortCombo.getItems().addAll("Relevance", "Price: Low to High", "Price: High to Low",
                    "Rating", "Popularity");
            sortCombo.setValue("Relevance");
            sortCombo.setOnAction(e -> filterProducts());
        } catch (Exception e) {
            System.err.println("setupSortCombo error: " + e.getMessage());
        }
    }

    private void filterProducts() {
        try {
            String keyword  = searchField.getText().trim();
            String category = categoryCombo.getValue();
            String sort     = sortCombo.getValue();

            List<Product> products;

            // Get base list by sort
            switch (sort != null ? sort : "Relevance") {
                case "Price: Low to High":
                    products = productService.getProductsSortedByPrice();
                    break;
                case "Price: High to Low":
                    List<Product> desc = productService.getProductsSortedByPrice();
                    java.util.Collections.reverse(desc);
                    products = desc;
                    break;
                case "Rating":
                    products = productService.getProductsSortedByRating();
                    break;
                case "Popularity":
                    products = productService.getProductsSortedByPopularity();
                    break;
                default:
                    products = productService.getAllProducts();
            }

            // Filter by category
            if (category != null && !category.equals("All Categories")) {
                products.removeIf(p -> !p.getCategory().equalsIgnoreCase(category));
            }

            // Filter by keyword
            if (!keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                products.removeIf(p -> !p.getName().toLowerCase().contains(kw)
                        && !p.getDescription().toLowerCase().contains(kw)
                        && !p.getCategory().toLowerCase().contains(kw));
            }

            loadProducts(products);
        } catch (Exception e) {
            System.err.println("filterProducts error: " + e.getMessage());
        }
    }

    private void loadProducts(List<Product> products) {
        try {
            productGrid.getChildren().clear();
            if (products.isEmpty()) {
                Label empty = new Label("No products found.");
                empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
                productGrid.getChildren().add(empty);
                return;
            }
            for (Product p : products) {
                productGrid.getChildren().add(createProductCard(p));
            }
        } catch (Exception e) {
            System.err.println("loadProducts error: " + e.getMessage());
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(230);
        card.setPadding(new Insets(16));

        // Category badge
        Label catBadge = new Label(product.getCategory());
        catBadge.getStyleClass().add("category-badge");

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        // Stock label
        Label stockLabel = new Label(product.getStock() > 0 ? "✓ In Stock (" + product.getStock() + ")" : "✗ Out of Stock");
        stockLabel.getStyleClass().add(product.getStock() > 0 ? "in-stock" : "out-stock");

        // Price
        Label priceLabel = new Label(String.format("₹%.2f", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Rating stars
        int stars = (int) Math.round(product.getRating());
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 5; i++) starStr.append(i < stars ? "★" : "☆");
        Label ratingLabel = new Label(starStr + String.format("  %.1f (%d reviews)", product.getRating(), product.getReviewCount()));
        ratingLabel.getStyleClass().add("product-rating");

        // Description (truncated)
        String desc = product.getDescription();
        if (desc != null && desc.length() > 60) desc = desc.substring(0, 60) + "…";
        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("product-desc");
        descLabel.setWrapText(true);

        // Buttons row
        Button addBtn = new Button("🛒 Add to Cart");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setDisable(product.getStock() == 0);
        addBtn.setMaxWidth(Double.MAX_VALUE);

        Button detailBtn = new Button("View Details");
        detailBtn.getStyleClass().add("btn-outline");
        detailBtn.setMaxWidth(Double.MAX_VALUE);

        addBtn.setOnAction(e -> {
            try {
                cartService.addToCart(product, 1);
                updateCartCount();
                addBtn.setText("✓ Added!");
                addBtn.getStyleClass().add("btn-success");
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5)) {{
                    setOnFinished(ev -> {
                        addBtn.setText("🛒 Add to Cart");
                        addBtn.getStyleClass().remove("btn-success");
                    });
                    play();
                }};
            } catch (Exception ex) {
                System.err.println("addToCart error: " + ex.getMessage());
            }
        });

        detailBtn.setOnAction(e -> showProductDetail(product));

        VBox.setVgrow(descLabel, Priority.ALWAYS);
        card.getChildren().addAll(catBadge, nameLabel, stockLabel, priceLabel, ratingLabel, descLabel, addBtn, detailBtn);
        return card;
    }

    private void showProductDetail(Product product) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle(product.getName());
            VBox root = new VBox(14);
            root.setPadding(new Insets(24));
            root.getStyleClass().add("detail-dialog");

            Label name  = new Label(product.getName());
            name.getStyleClass().add("detail-title");

            Label cat   = new Label("Category: " + product.getCategory());
            cat.getStyleClass().add("detail-meta");

            Label price = new Label("Price: ₹" + String.format("%.2f", product.getPrice()));
            price.getStyleClass().add("detail-price");

            Label stock = new Label("Stock: " + (product.getStock() > 0 ? product.getStock() + " units" : "Out of Stock"));
            Label rating = new Label("Rating: " + product.getRating() + "/5 (" + product.getReviewCount() + " reviews)");

            Label desc  = new Label(product.getDescription());
            desc.setWrapText(true);
            desc.getStyleClass().add("detail-desc");

            Button addBtn = new Button("🛒 Add to Cart");
            addBtn.getStyleClass().add("btn-primary");
            addBtn.setDisable(product.getStock() == 0);
            addBtn.setOnAction(e -> {
                cartService.addToCart(product, 1);
                updateCartCount();
                dialog.close();
            });

            Button closeBtn = new Button("Close");
            closeBtn.getStyleClass().add("btn-outline");
            closeBtn.setOnAction(e -> dialog.close());

            HBox btnRow = new HBox(10, addBtn, closeBtn);
            btnRow.setAlignment(Pos.CENTER_LEFT);

            root.getChildren().addAll(name, cat, price, stock, rating, new Separator(), desc, btnRow);

            Scene scene = new Scene(root, 420, 360);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.initOwner(cartButton.getScene().getWindow());
            dialog.show();
        } catch (Exception e) {
            System.err.println("showProductDetail error: " + e.getMessage());
        }
    }

    private void updateCartCount() {
        try {
            int count = cartService.getCartItemCount();
            cartButton.setText("🛒 Cart (" + count + ")");
        } catch (Exception e) {
            System.err.println("updateCartCount error: " + e.getMessage());
        }
    }

    @FXML private void goToCart() { navigate("/fxml/checkout.fxml"); }

    @FXML private void goToOrders() { navigate("/fxml/home.fxml"); }

    @FXML private void goToAdmin() {
        try {
            if (!UserSession.getInstance().isAdmin()) {
                showStatus("Access denied. Admin only.");
                return;
            }
            navigate("/fxml/register.fxml");
        } catch (Exception e) { System.err.println("goToAdmin error: " + e.getMessage()); }
    }

    @FXML private void handleLogout() {
        try {
            UserSession.getInstance().logout();
            CartService.reset();
            navigate("/fxml/login.fxml");
        } catch (Exception e) { System.err.println("logout error: " + e.getMessage()); }
    }

    private void navigate(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1100, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) cartButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("navigate error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showStatus(String msg) {
        try {
            statusLabel.setText(msg);
            statusLabel.setVisible(true);
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}