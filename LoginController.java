package com.shopping.controllers;

import com.shopping.models.Customer;
import com.shopping.models.UserSession;
import com.shopping.services.CustomerService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private TabPane tabPane;
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;
    @FXML private Button loginButton;

    @FXML private TextField regFirstNameField;
    @FXML private TextField regLastNameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private Label regErrorLabel;
    @FXML private Button registerButton;

    private CustomerService customerService;

    @FXML
    public void initialize() {
        try {
            customerService = CustomerService.getInstance();
            loginErrorLabel.setVisible(false);
            regErrorLabel.setVisible(false);
        } catch (Exception e) {
            System.err.println("LoginController init error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        try {
            String email = loginEmailField.getText().trim();
            String password = loginPasswordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showError(loginErrorLabel, "Please enter email and password.");
                return;
            }

            Customer customer = customerService.login(email, password);
            if (customer == null) {
                showError(loginErrorLabel, "Invalid email or password.");
                return;
            }

            UserSession.getInstance().login(customer);
            navigateToHome();
        } catch (Exception e) {
            showError(loginErrorLabel, "Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            String firstName = regFirstNameField.getText().trim();
            String lastName  = regLastNameField.getText().trim();
            String email     = regEmailField.getText().trim();
            String password  = regPasswordField.getText();
            String confirm   = regConfirmPasswordField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(regErrorLabel, "All fields are required.");
                return;
            }
            if (!password.equals(confirm)) {
                showError(regErrorLabel, "Passwords do not match.");
                return;
            }
            if (password.length() < 6) {
                showError(regErrorLabel, "Password must be at least 6 characters.");
                return;
            }
            if (customerService.emailExists(email)) {
                showError(regErrorLabel, "Email already registered.");
                return;
            }

            Customer customer = customerService.register(email, password, firstName, lastName);
            if (customer != null) {
                UserSession.getInstance().login(customer);
                navigateToHome();
            } else {
                showError(regErrorLabel, "Registration failed. Try again.");
            }
        } catch (Exception e) {
            showError(regErrorLabel, "Registration error: " + e.getMessage());
        }
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/orders.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Online Shopping System");
        } catch (Exception e) {
            showError(loginErrorLabel, "Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(Label label, String message) {
        try {
            label.setText(message);
            label.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(300), label);
            ft.setFromValue(0); ft.setToValue(1); ft.play();
        } catch (Exception e) {
            System.err.println("showError: " + e.getMessage());
        }
    }
}