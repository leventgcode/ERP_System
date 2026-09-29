module com.example.erp_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.erp_system to javafx.fxml;
    exports com.example.erp_system;
}