package com.example.erp_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    protected void onLogoutButtonClick(ActionEvent event) {
        try {
            // Mevcut (Dashboard) penceresini kapat
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            // Giriş (Login) ekranını tekrar yükle ve aç
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