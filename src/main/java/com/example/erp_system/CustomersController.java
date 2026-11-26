package com.example.erp_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.util.Locale;

public class CustomersController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField searchField;
    @FXML private Label messageLabel;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colAddress;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private final Locale trLocale = Locale.forLanguageTag("tr");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        setupSearchFilter();
        loadCustomerData();
    }

    private void setupSearchFilter() {
        FilteredList<Customer> filteredData = new FilteredList<>(customerList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String keyword = newValue.toLowerCase(trLocale).trim();

                return containsIgnoreCase(customer.getFullName(), keyword)
                        || containsIgnoreCase(customer.getPhone(), keyword)
                        || containsIgnoreCase(customer.getEmail(), keyword)
                        || containsIgnoreCase(customer.getAddress(), keyword);
            });
        });

        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedData);
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        return field != null && field.toLowerCase(trLocale).contains(keyword);
    }

    private void loadCustomerData() {
        customerList.clear();
        String query = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                customerList.add(new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address")
                ));
            }

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

            if (preparedStatement.executeUpdate() > 0) {
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

    @FXML
    protected void onDeleteCustomerClick(ActionEvent event) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Hata: Lütfen silmek için tablodan bir müşteri seçin!");
            return;
        }

        String query = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, selectedCustomer.getId());

            if (preparedStatement.executeUpdate() > 0) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Başarılı: Müşteri sistemden silindi!");
                loadCustomerData();
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