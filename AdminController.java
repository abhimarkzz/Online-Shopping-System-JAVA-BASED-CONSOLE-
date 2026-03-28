package com.shopping.controllers;

import com.shopping.Main;
import com.shopping.datastructures.SortingAlgorithms;
import com.shopping.models.*;
import com.shopping.services.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    // ── Stats ─────────────────────────────────────────────────────────────────
    @FXML private Label statsLabel;
    @FXML private TabPane adminTabs;

    // ── Products Tab ──────────────────────────────────────────────────────────
    @FXML private TableView<Product>               productsTable;
    @FXML private TableColumn<Product, String>     pColId, pColName, pColCategory, pColPrice, pColQty, pColRating;
    @FXML private TableColumn<Product, Void>       pColEdit, pColDelete;
    @FXML private TextField productSearchField;
    @FXML private TextField pNameField, pCategoryField, pPriceField, pQtyField, pRatingField, pDescField;
    @FXML private Label     productFormTitle, productMsg;
    @FXML private Button    saveProductBtn;
    private Product editingProduct = null;

    // ── Orders Tab ────────────────────────────────────────────────────────────
    @FXML private TableView<Order>               ordersAdminTable;
    @FXML private TableColumn<Order, String>     oColId, oColCustomer, oColItems, oColTotal, oColStatus, oColDate;
    @FXML private TableColumn<Order, Void>       oColUpdate;
    @FXML private Label pendingLabel;

    // ── Customers Tab ─────────────────────────────────────────────────────────
    @FXML private TableView<Customer>            customersTable;
    @FXML private TableColumn<Customer, String>  cColId, cColUsername, cColName, cColEmail, cColPhone, cColAdmin, cColDate;

    // ── Complaints Tab ────────────────────────────────────────────────────────
    @FXML private TableView<Complaint>           complaintsAdminTable;
    @FXML private TableColumn<Complaint, String> cmColId, cmColCustomer, cmColSubject, cmColOrder, cmColStatus, cmColDate;
    @FXML private TableColumn<Complaint, Void>   cmColResolve;

    // ── DSA Tab ───────────────────────────────────────────────────────────────
    @FXML private ComboBox<String> dsaAlgoCombo, dsaSortFieldCombo;
    @FXML private TextArea         dsaOutputArea;
    @FXML private Label            dsaTimeLabel;

    private final ProductService   productService   = ServiceLocator.getInstance().getProductService();
    private final OrderService     orderService     = ServiceLocator.getInstance().getOrderService();
    private final CustomerService  customerService  = ServiceLocator.getInstance().getCustomerService();
    private final ComplaintService complaintService = ServiceLocator.getInstance().getComplaintService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupProductsTab();
            setupOrdersTab();
            setupCustomersTab();
            setupComplaintsTab();
            setupDsaTab();
            updateStats();
        } catch (Exception e) {
            System.err.println("[AdminController] initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRODUCTS TAB
    // ══════════════════════════════════════════════════════════════════════════

    private void setupProductsTab() {
        try {
            pColId      .setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getId())));
            pColName    .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
            pColCategory.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategory()));
            pColPrice   .setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getPrice())));
            pColQty     .setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getQuantity())));
            pColRating  .setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getRating())));

            pColEdit.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("✏ Edit");
                { btn.setStyle("-fx-background-color:#3b82f6;-fx-text-fill:white;-fx-background-radius:4px;-fx-padding:3 8 3 8;-fx-cursor:hand;");
                    btn.setOnAction(e -> loadProductForEdit(getTableView().getItems().get(getIndex()))); }
                @Override protected void updateItem(Void v, boolean empty) {
                    super.updateItem(v, empty); setGraphic(empty ? null : btn); }
            });

            pColDelete.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("🗑 Del");
                { btn.setStyle("-fx-background-color:#ef4444;-fx-text-fill:white;-fx-background-radius:4px;-fx-padding:3 8 3 8;-fx-cursor:hand;");
                    btn.setOnAction(e -> deleteProduct(getTableView().getItems().get(getIndex()))); }
                @Override protected void updateItem(Void v, boolean empty) {
                    super.updateItem(v, empty); setGraphic(empty ? null : btn); }
            });

            refreshProductsTable(productService.getAllProducts());
        } catch (Exception e) {
            System.err.println("[AdminController] setupProductsTab: " + e.getMessage());
        }
    }

    private void refreshProductsTable(List<Product> list) {
        try { productsTable.setItems(FXCollections.observableArrayList(list)); }
        catch (Exception e) { System.err.println("[AdminController] refreshProductsTable: " + e.getMessage()); }
    }

    @FXML private void handleProductSearch() {
        try {
            String q = productSearchField.getText().trim();
            refreshProductsTable(q.isEmpty() ? productService.getAllProducts() : productService.search(q));
        } catch (Exception e) { System.err.println("[AdminController] productSearch: " + e.getMessage()); }
    }

    @FXML private void handleAddProduct() {
        editingProduct = null;
        productFormTitle.setText("Add New Product");
        saveProductBtn.setText("💾  Save Product");
        clearProductForm();
    }

    private void loadProductForEdit(Product p) {
        try {
            editingProduct = p;
            productFormTitle.setText("Edit Product: " + p.getName());
            saveProductBtn.setText("💾  Update Product");
            pNameField    .setText(p.getName());
            pCategoryField.setText(p.getCategory());
            pPriceField   .setText(String.valueOf(p.getPrice()));
            pQtyField     .setText(String.valueOf(p.getQuantity()));
            pRatingField  .setText(String.valueOf(p.getRating()));
            pDescField    .setText(p.getDescription());
        } catch (Exception e) { System.err.println("[AdminController] loadProductForEdit: " + e.getMessage()); }
    }

    @FXML private void handleSaveProduct() {
        try {
            String name  = pNameField.getText().trim();
            String cat   = pCategoryField.getText().trim();
            String price = pPriceField.getText().trim();
            String qty   = pQtyField.getText().trim();
            String rating= pRatingField.getText().trim();
            String desc  = pDescField.getText().trim();

            if (name.isEmpty() || cat.isEmpty() || price.isEmpty() || qty.isEmpty()) {
                productMsg.setText("Please fill in all required fields.");
                productMsg.setStyle("-fx-text-fill:#e94560;");
                return;
            }
            double priceVal  = Double.parseDouble(price);
            int    qtyVal    = Integer.parseInt(qty);
            double ratingVal = rating.isEmpty() ? 0.0 : Double.parseDouble(rating);

            if (editingProduct == null) {
                Product p = new Product(0, name, priceVal, cat, desc, qtyVal, ratingVal, "");
                productService.addProduct(p);
                productMsg.setText("Product added successfully!");
            } else {
                editingProduct.setName(name);
                editingProduct.setCategory(cat);
                editingProduct.setPrice(priceVal);
                editingProduct.setQuantity(qtyVal);
                editingProduct.setRating(ratingVal);
                editingProduct.setDescription(desc);
                productService.updateProduct(editingProduct);
                productMsg.setText("Product updated successfully!");
                editingProduct = null;
            }
            productMsg.setStyle("-fx-text-fill:#10b981;");
            clearProductForm();
            refreshProductsTable(productService.getAllProducts());
            updateStats();
        } catch (NumberFormatException e) {
            productMsg.setText("Invalid number format in price/qty/rating.");
            productMsg.setStyle("-fx-text-fill:#e94560;");
        } catch (Exception e) {
            productMsg.setText("Error: " + e.getMessage());
            productMsg.setStyle("-fx-text-fill:#e94560;");
            e.printStackTrace();
        }
    }

    @FXML private void handleCancelProduct() { editingProduct = null; clearProductForm(); }

    private void deleteProduct(Product p) {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + p.getName() + "\"?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    productService.removeProduct(p.getId());
                    refreshProductsTable(productService.getAllProducts());
                    updateStats();
                }
            });
        } catch (Exception e) { System.err.println("[AdminController] deleteProduct: " + e.getMessage()); }
    }

    private void clearProductForm() {
        try {
            pNameField.clear(); pCategoryField.clear(); pPriceField.clear();
            pQtyField.clear();  pRatingField.clear();   pDescField.clear();
            productMsg.setText("");
        } catch (Exception e) { System.err.println("[AdminController] clearProductForm: " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ORDERS TAB
    // ══════════════════════════════════════════════════════════════════════════

    private void setupOrdersTab() {
        try {
            oColId      .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrderId()));
            oColCustomer.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCustomerName()));
            oColItems   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getItemsSummary()));
            oColTotal   .setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getTotalAmount())));
            oColStatus  .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus().name()));
            oColDate    .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrderDate()));

            oColUpdate.setCellFactory(col -> new TableCell<>() {
                private final ComboBox<String> combo = new ComboBox<>();
                private final Button btn = new Button("Set");
                private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(4, combo, btn);
                {
                    combo.getItems().addAll("PENDING","PROCESSING","SHIPPED","DELIVERED","CANCELLED");
                    combo.setPrefWidth(110);
                    btn.setStyle("-fx-background-color:#3b82f6;-fx-text-fill:white;-fx-background-radius:4px;-fx-padding:2 6 2 6;-fx-cursor:hand;");
                    btn.setOnAction(e -> {
                        Order order = getTableView().getItems().get(getIndex());
                        String selected = combo.getValue();
                        if (selected != null) {
                            orderService.updateStatus(order.getOrderId(), Order.Status.valueOf(selected));
                            refreshOrdersTable();
                            updateStats();
                        }
                    });
                }
                @Override protected void updateItem(Void v, boolean empty) {
                    super.updateItem(v, empty);
                    if (empty) { setGraphic(null); return; }
                    Order o = getTableView().getItems().get(getIndex());
                    combo.setValue(o.getStatus().name());
                    setGraphic(box);
                }
            });

            refreshOrdersTable();
        } catch (Exception e) {
            System.err.println("[AdminController] setupOrdersTab: " + e.getMessage());
        }
    }

    private void refreshOrdersTable() {
        try {
            ordersAdminTable.setItems(FXCollections.observableArrayList(orderService.getAllOrders()));
            pendingLabel.setText(orderService.getPendingCount() + " pending");
        } catch (Exception e) { System.err.println("[AdminController] refreshOrdersTable: " + e.getMessage()); }
    }

    @FXML private void handleProcessNext() {
        try {
            Order o = orderService.processNextOrder();
            if (o != null) {
                new Alert(Alert.AlertType.INFORMATION, "Processing order: " + o.getOrderId()).showAndWait();
                refreshOrdersTable();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "No pending orders in queue.").showAndWait();
            }
        } catch (Exception e) { System.err.println("[AdminController] processNext: " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CUSTOMERS TAB
    // ══════════════════════════════════════════════════════════════════════════

    private void setupCustomersTab() {
        try {
            cColId      .setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getId())));
            cColUsername.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUsername()));
            cColName    .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
            cColEmail   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail()));
            cColPhone   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPhone() != null ? cd.getValue().getPhone() : ""));
            cColAdmin   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().isAdmin() ? "Admin" : "Customer"));
            cColDate    .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRegisteredDate() != null ? cd.getValue().getRegisteredDate() : ""));
            customersTable.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        } catch (Exception e) {
            System.err.println("[AdminController] setupCustomersTab: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // COMPLAINTS TAB
    // ══════════════════════════════════════════════════════════════════════════

    private void setupComplaintsTab() {
        try {
            cmColId      .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getComplaintId()));
            cmColCustomer.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCustomerName()));
            cmColSubject .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubject()));
            cmColOrder   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrderId() != null ? cd.getValue().getOrderId() : "-"));
            cmColStatus  .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus().name()));
            cmColDate    .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubmittedDate()));

            cmColResolve.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("✅ Resolve");
                { btn.setStyle("-fx-background-color:#10b981;-fx-text-fill:white;-fx-background-radius:4px;-fx-padding:3 6 3 6;-fx-cursor:hand;");
                    btn.setOnAction(e -> {
                        Complaint c = getTableView().getItems().get(getIndex());
                        TextInputDialog dlg = new TextInputDialog("Issue resolved.");
                        dlg.setTitle("Resolve Complaint");
                        dlg.setHeaderText("Resolution for: " + c.getComplaintId());
                        dlg.setContentText("Enter resolution:");
                        dlg.showAndWait().ifPresent(res -> {
                            complaintService.resolveComplaint(c.getComplaintId(), res);
                            refreshComplaintsTable();
                        });
                    }); }
                @Override protected void updateItem(Void v, boolean empty) {
                    super.updateItem(v, empty); setGraphic(empty ? null : btn); }
            });

            refreshComplaintsTable();
        } catch (Exception e) {
            System.err.println("[AdminController] setupComplaintsTab: " + e.getMessage());
        }
    }

    private void refreshComplaintsTable() {
        try {
            complaintsAdminTable.setItems(FXCollections.observableArrayList(complaintService.getAllComplaints()));
        } catch (Exception e) { System.err.println("[AdminController] refreshComplaintsTable: " + e.getMessage()); }
    }

    @FXML private void handleProcessComplaint() {
        try {
            Complaint c = complaintService.processNextComplaint();
            if (c != null) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Processing complaint: " + c.getComplaintId() + "\nSubject: " + c.getSubject()).showAndWait();
                refreshComplaintsTable();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "No open complaints in queue.").showAndWait();
            }
        } catch (Exception e) { System.err.println("[AdminController] processComplaint: " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DSA DEMO TAB
    // ══════════════════════════════════════════════════════════════════════════

    private void setupDsaTab() {
        try {
            dsaAlgoCombo.getItems().addAll(
                    "Bubble Sort", "Selection Sort", "Insertion Sort",
                    "Merge Sort", "Quick Sort", "Heap Sort", "Shell Sort");
            dsaAlgoCombo.getSelectionModel().select("Heap Sort");

            dsaSortFieldCombo.getItems().addAll(
                    "Price Ascending", "Price Descending", "Name A-Z", "Rating", "Category");
            dsaSortFieldCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("[AdminController] setupDsaTab: " + e.getMessage());
        }
    }

    @FXML private void handleDsaRun() {
        try {
            String algoStr  = dsaAlgoCombo.getValue();
            String fieldStr = dsaSortFieldCombo.getValue();
            if (algoStr == null || fieldStr == null) { dsaOutputArea.setText("Please select algorithm and field."); return; }

            SortingAlgorithms.Algorithm algo = algoFromString(algoStr);
            java.util.Comparator<com.shopping.models.Product> cmp = comparatorFromField(fieldStr);

            java.util.List<com.shopping.models.Product> products = productService.getAllProducts();

            long start = System.nanoTime();
            java.util.List<com.shopping.models.Product> sorted = SortingAlgorithms.sort(products, cmp, algo);
            long elapsed = System.nanoTime() - start;

            dsaTimeLabel.setText(String.format("⏱  %.3f ms  (%d items)", elapsed / 1_000_000.0, sorted.size()));
            dsaTimeLabel.setStyle("-fx-text-fill:#10b981; -fx-font-weight:bold;");

            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════════════════════════════════════════════╗\n");
            sb.append(String.format("║  Algorithm : %-58s ║\n", algoStr));
            sb.append(String.format("║  Sort By   : %-58s ║\n", fieldStr));
            sb.append(String.format("║  Items     : %-58s ║\n", sorted.size()));
            sb.append(String.format("║  Time      : %-58s ║\n", String.format("%.3f ms", elapsed / 1_000_000.0)));
            sb.append("╠══╦═══════════════════════════════════╦═══════════════╦════════╦════════╣\n");
            sb.append("║# ║ Name                              ║ Category      ║  Price ║ Rating ║\n");
            sb.append("╠══╬═══════════════════════════════════╬═══════════════╬════════╬════════╣\n");

            int rank = 1;
            for (com.shopping.models.Product p : sorted) {
                sb.append(String.format("║%-2d║ %-33s ║ %-13s ║ $%6.2f ║  %-5.1f ║\n",
                        rank++, truncate(p.getName(), 33), truncate(p.getCategory(), 13),
                        p.getPrice(), p.getRating()));
            }
            sb.append("╚══╩═══════════════════════════════════╩═══════════════╩════════╩════════╝\n\n");

            // LinkedList demo
            sb.append("── CustomLinkedList (doubly-linked, size=").append(sorted.size()).append(") ──\n");
            sb.append("Head: ").append(sorted.isEmpty() ? "null" : sorted.get(0).getName()).append("  ←→  ... ←→  ");
            sb.append(sorted.size() > 1 ? sorted.get(sorted.size()-1).getName() : "").append(" :Tail\n\n");

            // HashTable demo
            sb.append("── HashTable (product lookup by ID) ──\n");
            sb.append("Lookup product ID=1  →  ").append(productService.getById(1) != null ? productService.getById(1).getName() : "Not found").append("\n");
            sb.append("Lookup product ID=5  →  ").append(productService.getById(5) != null ? productService.getById(5).getName() : "Not found").append("\n\n");

            // Queue demo
            sb.append("── CustomQueue (pending orders FIFO) ──\n");
            sb.append("Pending orders in queue: ").append(orderService.getPendingCount()).append("\n\n");

            // Stack demo
            sb.append("── CustomStack (cart undo) ──\n");
            sb.append("Stack is LIFO — last added cart item can be undone\n");

            dsaOutputArea.setText(sb.toString());

        } catch (Exception e) {
            dsaOutputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max-1) + "…" : s;
    }

    private SortingAlgorithms.Algorithm algoFromString(String s) {
        return switch (s) {
            case "Bubble Sort"    -> SortingAlgorithms.Algorithm.BUBBLE;
            case "Selection Sort" -> SortingAlgorithms.Algorithm.SELECTION;
            case "Insertion Sort" -> SortingAlgorithms.Algorithm.INSERTION;
            case "Quick Sort"     -> SortingAlgorithms.Algorithm.QUICK;
            case "Heap Sort"      -> SortingAlgorithms.Algorithm.HEAP;
            case "Shell Sort"     -> SortingAlgorithms.Algorithm.SHELL;
            default               -> SortingAlgorithms.Algorithm.MERGE;
        };
    }

    private java.util.Comparator<com.shopping.models.Product> comparatorFromField(String s) {
        return switch (s) {
            case "Price Descending" -> SortingAlgorithms.BY_PRICE_DESC;
            case "Name A-Z"         -> SortingAlgorithms.BY_NAME_ASC;
            case "Rating"           -> SortingAlgorithms.BY_RATING_DESC;
            case "Category"         -> SortingAlgorithms.BY_CATEGORY;
            default                 -> SortingAlgorithms.BY_PRICE_ASC;
        };
    }

    private void updateStats() {
        try {
            statsLabel.setText(String.format(
                    "Products: %d  |  Orders: %d  |  Customers: %d  |  Pending: %d",
                    productService.size(), orderService.getTotalCount(),
                    customerService.size(), orderService.getPendingCount()));
        } catch (Exception e) { System.err.println("[AdminController] updateStats: " + e.getMessage()); }
    }

    @FXML private void handleBackToShop() { Main.switchScene("home.fxml"); }
}