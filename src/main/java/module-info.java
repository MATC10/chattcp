module com.example.whatsappexamen {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;

    opens com.example.whatsappexamennuevo to javafx.fxml;
    exports com.example.whatsappexamennuevo;
}