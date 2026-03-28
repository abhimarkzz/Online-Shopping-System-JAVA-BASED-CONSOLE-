package com.shopping.controllers;

import com.shopping.Main;
import com.shopping.models.Order;
import com.shopping.models.UserSession;
import com.shopping.services.OrderService;
import com.shopping.services.ServiceLocator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {

    @FXML private TableView<Order>               ordersTable;
    @FXML private TableColumn<Order, String>     colOrderId;
    @FXML private TableColumn<Order, String>     colDate;
    @FXML private TableColumn<Order, String>     colItems;
    @FXML private TableColumn<Order, String>     colTotal;
    @FXML private TableColumn<Order, String>     colStatus;
    @FXML private TableColumn<Order, String>     colAddress;
    @FXML private VBox                           detailPanel;
    @FXML private TextArea                       detailArea;

    private final OrderService orderService = ServiceLocator.getInstance().getOrderService();
    private final UserSession  session      = UserSession.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupColumns();
            loadOrders();

            // Click row to show details
            ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, order) -> {
                if (order != null) showDetail(order);
            });
        } catch (Exception e) {
            System.err.println("[OrdersController] initialize: " + e.getMessage());
        }
    }

    private void setupColumns() {
        try {
            colOrderId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrderId()));
            colDate   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrderDate()));
            colItems  .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getItemsSummary()));
            colTotal  .setCellValueFactory(cd -> new SimpleStringProperty(
                    String.format("$%.2f", cd.getValue().getTotalAmount())));
            colStatus .setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getStatus().name()));
            colAddress.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getShippingAddress()));

            // Colour-code status column
            colStatus.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) { setText(null); setStyle(""); return; }
                    setText(status);
                    switch (status) {
                        case "PENDING"    -> setStyle("-fx-text-fill:#f59e0b; -fx-font-weight:bold;");
                        case "PROCESSING" -> setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold;");
                        case "SHIPPED"    -> setStyle("-fx-text-fill:#8b5cf6; -fx-font-weight:bold;");
                        case "DELIVERED"  -> setStyle("-fx-text-fill:#10b981; -fx-font-weight:bold;");
                        case "CANCELLED"  -> setStyle("-fx-text-fill:#ef4444; -fx-font-weight:bold;");
                        default           -> setStyle("");
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[OrdersController] setupColumns: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try {
            int custId = session.getCurrentCustomer().getId();
            List<Order> orders = orderService.getOrdersByCustomer(custId);
            ordersTable.setItems(FXCollections.observableArrayList(orders));
            if (orders.isEmpty()) {
                Label placeholder = new Label("No orders yet. Start shopping! 🛒");
                placeholder.setStyle("-fx-text-fill:#888; -fx-font-size:14px;");
                ordersTable.setPlaceholder(placeholder);
            }
        } catch (Exception e) {
            System.err.println("[OrdersController] loadOrders: " + e.getMessage());
        }
    }

    private void showDetail(Order order) {
        try {
            detailPanel.setVisible(true);
            detailPanel.setManaged(true);
            StringBuilder sb = new StringBuilder();
            sb.append("Order ID    : ").append(order.getOrderId()).append("\n");
            sb.append("Date        : ").append(order.getOrderDate()).append("\n");
            sb.append("Status      : ").append(order.getStatus()).append("\n");
            sb.append("Address     : ").append(order.getShippingAddress()).append("\n");
            sb.append("─────────────────────────────────────────\n");
            sb.append("Items:\n");
            for (var item : order.getItems()) {
                sb.append(String.format("  %-30s x%-3d  $%.2f\n",
                        item.getProduct().getName(), item.getQuantity(), item.getSubtotal()));
            }
            sb.append("─────────────────────────────────────────\n");
            sb.append(String.format("TOTAL: $%.2f\n", order.getTotalAmount()));
            detailArea.setText(sb.toString());
        } catch (Exception e) {
            System.err.println("[OrdersController] showDetail: " + e.getMessage());
        }
    }

    @FXML private void handleBackToShop()  { Main.switchScene("home.fxml"); }
    @FXML private void handleComplaint()   { Main.switchScene("complaints.fxml"); }
}