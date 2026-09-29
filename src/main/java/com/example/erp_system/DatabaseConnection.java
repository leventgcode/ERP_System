package com.example.erp_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Veritabanı bağlantı bilgileri
    private static final String URL = "jdbc:mysql://localhost:3306/erp_db";
    private static final String USER = "root";
    // DİKKAT: Aşağıdaki tırnakların içine kendi MySQL şifreni yazmalısın!
    private static final String PASSWORD = "hbk87yNXPV*";

    // Bağlantı nesnemiz (Singleton yapısı için statik tanımlıyoruz)
    private static Connection connection = null;

    // Veritabanı bağlantısını çağıran metodumuz
    public static Connection getConnection() {
        try {
            // Eğer bağlantı daha önce oluşturulmamışsa veya kapanmışsa yeni bağlantı aç
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Harika! Veritabanı bağlantısı BAŞARILI!");
            }
        } catch (SQLException e) {
            System.out.println("HATA: Veritabanına bağlanılamadı!");
            e.printStackTrace();
        }
        return connection;
    }

    // Bağlantıyı sadece test etmek için geçici bir main metodu
    public static void main(String[] args) {
        getConnection();
    }
}