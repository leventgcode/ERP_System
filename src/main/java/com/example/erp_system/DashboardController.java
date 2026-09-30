package com.example.erp_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private BorderPane mainBorderPane; // Ana ekranın tamamını temsil eder

    @FXML
    private Button logoutButton;

    // --- MÜŞTERİLER BUTONUNA TIKLANINCA ÇALIŞACAK KOD ---
    @FXML
    protected void onCustomersButtonClick(ActionEvent event) {
        try {
            // customers.fxml dosyasını hafızaya yükle
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("customers.fxml"));
            Node customersView = fxmlLoader.load();

            // Yüklenen bu yeni sayfayı, ana ekranın tam ortasına (center) yerleştir
            mainBorderPane.setCenter(customersView);

        } catch (Exception e) {
            System.err.println("Müşteriler sayfası yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

    // --- ÇIKIŞ YAP BUTONUNA TIKLANINCA ÇALIŞACAK KOD ---
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 400);
            Stage loginStage = new Stage();
            loginStage.setTitle("ERP Sistemine Giriş");
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception e) {
            System.err.println("Çıkış yapılırken bir hata oluştu: " + e.getMessage());
        }
    }
}