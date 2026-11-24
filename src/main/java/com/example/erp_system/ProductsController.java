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
import java.sql.Types;

public class ProductsController {

    @FXML private TextField barcodeField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private Label messageLabel;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colBarcode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    private ObservableList<Product> productList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        // Tablodan bir ürüne tıklandığında bilgileri güncelleme için kutulara doldur
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                barcodeField.setText(newSelection.getBarcode() != null ? newSelection.getBarcode() : "");
                nameField.setText(newSelection.getName());
                priceField.setText(String.valueOf(newSelection.getPrice()));
                stockField.setText(String.valueOf(newSelection.getStockQuantity()));
            }
        });

        loadProductData();
    }

    private void loadProductData() {
        productList = FXCollections.observableArrayList();
        String query = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("barcode"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock_quantity")
                );
                productList.add(product);
            }
            productTable.setItems(productList);

        } catch (Exception e) {
            showMessage("Tablo yüklenirken hata oluştu!", true);
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddProductClick(ActionEvent event) {
        String barcode = barcodeField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim().replace(",", ".");
        String stockText = stockField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            showMessage("Hata: Ürün Adı, Fiyat ve Stok Adedi zorunludur!", true);
            return;
        }

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceText);
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showMessage("Hata: Fiyat ve Stok alanlarına geçerli sayılar girin!", true);
            return;
        }

        String query = "INSERT INTO products (barcode, name, price, stock_quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (barcode.isEmpty()) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, barcode);
            }
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);

            if (ps.executeUpdate() > 0) {
                showMessage("Başarılı: Ürün stoğa eklendi!", false);
                clearForm();
                loadProductData();
            }
        } catch (Exception e) {
            showMessage("Veritabanı Hatası: " + e.getMessage(), true);
        }
    }

    @FXML
    protected void onUpdateProductClick(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Hata: Güncellemek için tablodan bir ürün seçin!", true);
            return;
        }

        String barcode = barcodeField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim().replace(",", ".");
        String stockText = stockField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            showMessage("Hata: Ürün Adı, Fiyat ve Stok Adedi boş olamaz!", true);
            return;
        }

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceText);
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showMessage("Hata: Fiyat ve Stok alanlarına geçerli sayılar girin!", true);
            return;
        }

        String query = "UPDATE products SET barcode = ?, name = ?, price = ?, stock_quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (barcode.isEmpty()) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, barcode);
            }
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, selected.getId());

            if (ps.executeUpdate() > 0) {
                showMessage("Başarılı: Ürün bilgileri güncellendi!", false);
                clearForm();
                loadProductData();
            }
        } catch (Exception e) {
            showMessage("Veritabanı Hatası: " + e.getMessage(), true);
        }
    }

    @FXML
    protected void onDeleteProductClick(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Hata: Silmek için tablodan bir ürün seçin!", true);
            return;
        }

        String query = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, selected.getId());

            if (ps.executeUpdate() > 0) {
                showMessage("Başarılı: Ürün sistemden silindi!", false);
                clearForm();
                loadProductData();
            }
        } catch (Exception e) {
            showMessage("Veritabanı Hatası: " + e.getMessage(), true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    private void clearForm() {
        barcodeField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
    }
}