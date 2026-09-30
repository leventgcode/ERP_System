package com.example.erp_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CustomersController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private Label messageLabel;

    @FXML
    protected void onAddCustomerClick(ActionEvent event) {
        // İlerleyen adımlarda buraya veritabanına ekleme (INSERT) kodlarını yazacağız.
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Buton çalışıyor! Yakında veritabanına eklenecek.");
    }
}