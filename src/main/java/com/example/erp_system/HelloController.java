package com.example.erp_system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloController {

    // FXML dosyasındaki elemanları Java koduna bağlıyoruz
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // "Giriş Yap" butonuna tıklandığında çalışacak olan metod
    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Alanlar boş mu diye kontrol edelim
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Lütfen kullanıcı adı ve şifreyi girin!");
            return;
        }

        // Veritabanında kullanıcıyı arayalım
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                // SQL Sorgusu: Kullanıcı adı ve şifre eşleşiyor mu?
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                // Eğer eşleşen bir kayıt bulunursa giriş başarılıdır
                if (resultSet.next()) {
                    String role = resultSet.getString("role"); // Kullanıcının yetkisini çek (Admin/Kasiyer)
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Giriş Başarılı! Hoşgeldin, " + username + " (" + role + ")");

                    // TODO: İlerleyen aşamalarda buraya ERP Ana Ekranına (Dashboard) geçiş kodlarını yazacağız.
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("Hatalı kullanıcı adı veya şifre!");
                }
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Veritabanı bağlantı hatası!");
            e.printStackTrace();
        }
    }
}