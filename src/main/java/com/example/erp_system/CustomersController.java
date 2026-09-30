package com.example.erp_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomersController {

    // Arayüzdeki form kutucukları
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private Label messageLabel;

    // --- YENİ MÜŞTERİ EKLEME İŞLEMİ ---
    @FXML
    protected void onAddCustomerClick(ActionEvent event) {
        // 1. Kutucuklardaki yazıları alıyoruz (.trim() ile baştaki/sondaki boşlukları temizliyoruz)
        String fullName = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        // 2. Zorunlu alan kontrolü (Müşteri adı boş olamaz)
        if (fullName.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Hata: Ad Soyad alanı boş bırakılamaz!");
            return; // Kodun aşağıya inmesini durdur
        }

        // 3. Veritabanına Bağlan ve Ekleme İşlemini Yap
        String query = "INSERT INTO customers (full_name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            // Soru işaretlerinin (?) yerine formdan aldığımız verileri yerleştiriyoruz
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);

            // 4. Kodu MySQL'de çalıştırıyoruz
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Ekleme başarılıysa kullanıcıya bilgi ver ve form kutularını temizle
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Başarılı: Müşteri sisteme kaydedildi!");
                clearForm();
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

    // İşlem bitince kutucukları temizleyen yardımcı metot
    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
    }
}