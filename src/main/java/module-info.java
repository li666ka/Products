module com.example.ais_store {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    exports com.example.ais_store;
    opens com.example.ais_store to javafx.fxml;
    exports com.example.ais_store.Models;
    opens com.example.ais_store.Models to javafx.fxml;
    exports com.example.ais_store.TCP;
    opens com.example.ais_store.TCP to javafx.fxml;
}