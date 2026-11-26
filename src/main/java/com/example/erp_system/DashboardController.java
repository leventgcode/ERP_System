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
    private BorderPane mainBorderPane;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        loadView("home.fxml");
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {
        loadView("home.fxml");
    }

    @FXML
    protected void onCustomersButtonClick(ActionEvent event) {
        loadView("customers.fxml");
    }

    @FXML
    protected void onProductsButtonClick(ActionEvent event) {
        loadView("products.fxml");
    }

    @FXML
    protected void onInvoicesButtonClick(ActionEvent event) {
        loadView("invoices.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Node view = fxmlLoader.load();
            mainBorderPane.setCenter(view);
        } catch (Exception e) {
            System.err.println(fxmlFile + " yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

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