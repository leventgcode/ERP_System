package com.example.erp_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomersController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private Label messageLabel;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colAddress;

    private ObservableList<Customer> customerList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadCustomerData();
    }

    private void loadCustomerData() {
        customerList = FXCollections.observableArrayList();
        String query = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address")
                );
                customerList.add(customer);
            }
            customerTable.setItems(customerList);

        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Tablo yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddCustomerClick(ActionEvent event) {
        String fullName = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (fullName.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Hata: Ad Soyad alanı boş bırakılamaz!");
            return;
        }

        String query = "INSERT INTO customers (full_name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Başarılı: Müşteri sisteme kaydedildi!");
                clearForm();
                loadCustomerData();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Hata: Kayıt işlemi başarısız oldu.");
            }

        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Veritabanı Hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- SEÇİLİ MÜŞTERİYİ SİLME İŞLEMİ ---
    @FXML
    protected void onDeleteCustomerClick(ActionEvent event) {
        // Tablodan kullanıcının tıkladığı satırı al
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        // Eğer hiçbir satır seçilmeden butona basıldıysa uyar
        if (selectedCustomer == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Hata: Lütfen silmek için tablodan bir müşteri seçin!");
            return;
        }

        String query = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            // Seçilen müşterinin ID'sini sorguya gönder
            preparedStatement.setInt(1, selectedCustomer.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Başarılı: Müşteri sistemden silindi!");
                loadCustomerData(); // Tabloyu hemen güncelle
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Hata: Silme işlemi başarısız oldu.");
            }

        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Veritabanı Hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
    }
}