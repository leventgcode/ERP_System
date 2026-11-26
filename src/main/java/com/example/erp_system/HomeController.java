package com.example.erp_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class HomeController {

    @FXML private Label totalCustomersLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label criticalStockLabel;

    @FXML private TableView<Product> criticalStockTable;
    @FXML private TableColumn<Product, String> colBarcode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    @FXML
    public void initialize() {
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        loadStatistics();
        loadCriticalStockProducts();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM customers")) {
                if (rs.next()) {
                    totalCustomersLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products WHERE is_active = TRUE")) {
                if (rs.next()) {
                    totalProductsLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount), 0) FROM sales")) {
                if (rs.next()) {
                    totalRevenueLabel.setText(String.format("%,.2f TL", rs.getDouble(1)));
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products WHERE stock_quantity < 5 AND is_active = TRUE")) {
                if (rs.next()) {
                    criticalStockLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCriticalStockProducts() {
        ObservableList<Product> criticalList = FXCollections.observableArrayList();
        String query = "SELECT * FROM products WHERE stock_quantity < 5 AND is_active = TRUE ORDER BY stock_quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                criticalList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity")
                ));
            }
            criticalStockTable.setItems(criticalList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onResetRevenueClick(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ciroyu Sıfırla");
        confirm.setHeaderText("Tüm satış ve fatura geçmişi silinecek!");
        confirm.setContentText("Toplam ciroyu sıfırlamak istediğinizden emin misiniz? Bu işlem geri alınamaz.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement st = conn.createStatement()) {

                st.executeUpdate("DELETE FROM sales WHERE id > 0");
                loadStatistics();

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Başarılı");
                info.setHeaderText(null);
                info.setContentText("Toplam ciro ve satış geçmişi başarıyla sıfırlandı.");
                info.showAndWait();

            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Hata");
                error.setHeaderText("Ciro sıfırlanırken bir hata oluştu!");
                error.setContentText(e.getMessage());
                error.showAndWait();
                e.printStackTrace();
            }
        }
    }
}