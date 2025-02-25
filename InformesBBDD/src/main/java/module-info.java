module com.example.informesbbdd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jasperreports;
    requires org.slf4j;

    opens com.example.informesbbdd to javafx.fxml;
    exports com.example.informesbbdd;
}