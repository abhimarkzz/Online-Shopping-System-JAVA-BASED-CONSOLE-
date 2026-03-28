package com.shopping.controllers;

import com.shopping.Main;
import com.shopping.models.Complaint;
import com.shopping.models.UserSession;
import com.shopping.services.ComplaintService;
import com.shopping.services.ServiceLocator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ComplaintsController implements Initializable {

    @FXML private TextField  orderIdField;
    @FXML private TextField  subjectField;
    @FXML private TextArea   descField;
    @FXML private Label      submitMsg;

    @FXML private TableView<Complaint>               complaintsTable;
    @FXML private TableColumn<Complaint, String>     colId;
    @FXML private TableColumn<Complaint, String>     colSubject;
    @FXML private TableColumn<Complaint, String>     colOrder;
    @FXML private TableColumn<Complaint, String>     colStatus;
    @FXML private TableColumn<Complaint, String>     colDate;

    private final ComplaintService complaintService = ServiceLocator.getInstance().getComplaintService();
    private final UserSession      session          = UserSession.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupColumns();
            loadComplaints();
        } catch (Exception e) {
            System.err.println("[ComplaintsController] initialize: " + e.getMessage());
        }
    }

    private void setupColumns() {
        try {
            colId     .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getComplaintId()));
            colSubject.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubject()));
            colOrder  .setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getOrderId() != null ? cd.getValue().getOrderId() : "-"));
            colStatus .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus().name()));
            colDate   .setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubmittedDate()));

            // colour-code status
            colStatus.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(String s, boolean empty) {
                    super.updateItem(s, empty);
                    if (empty || s == null) { setText(null); setStyle(""); return; }
                    setText(s);
                    switch (s) {
                        case "OPEN"      -> setStyle("-fx-text-fill:#ef4444; -fx-font-weight:bold;");
                        case "IN_REVIEW" -> setStyle("-fx-text-fill:#3b82f6; -fx-font-weight:bold;");
                        case "RESOLVED"  -> setStyle("-fx-text-fill:#10b981; -fx-font-weight:bold;");
                        case "DISMISSED" -> setStyle("-fx-text-fill:#888;");
                        default          -> setStyle("");
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[ComplaintsController] setupColumns: " + e.getMessage());
        }
    }

    private void loadComplaints() {
        try {
            int custId = session.getCurrentCustomer().getId();
            List<Complaint> complaints = complaintService.getComplaintsByCustomer(custId);
            complaintsTable.setItems(FXCollections.observableArrayList(complaints));
            if (complaints.isEmpty()) {
                Label ph = new Label("No complaints filed yet.");
                ph.setStyle("-fx-text-fill:#888; -fx-font-size:13px;");
                complaintsTable.setPlaceholder(ph);
            }
        } catch (Exception e) {
            System.err.println("[ComplaintsController] loadComplaints: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            String subject = subjectField.getText().trim();
            String desc    = descField.getText().trim();
            String orderId = orderIdField.getText().trim();

            if (subject.isEmpty()) { setMsg("Please enter a subject.", false); return; }
            if (desc.isEmpty())    { setMsg("Please enter a description.", false); return; }

            var customer = session.getCurrentCustomer();
            Complaint c  = complaintService.submitComplaint(
                    customer.getId(),
                    customer.getFullName(),
                    orderId.isEmpty() ? null : orderId,
                    subject,
                    desc
            );

            if (c != null) {
                setMsg("Complaint " + c.getComplaintId() + " submitted successfully!", true);
                subjectField.clear();
                descField.clear();
                orderIdField.clear();
                loadComplaints();
            } else {
                setMsg("Failed to submit. Please try again.", false);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML private void handleBack() { Main.switchScene("home.fxml"); }

    private void setMsg(String msg, boolean success) {
        if (submitMsg != null) {
            submitMsg.setText(msg);
            submitMsg.setStyle(success
                    ? "-fx-text-fill:#10b981; -fx-font-weight:bold;"
                    : "-fx-text-fill:#e94560; -fx-font-weight:bold;");
        }
    }
}