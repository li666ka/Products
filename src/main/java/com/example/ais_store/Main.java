package com.example.ais_store;
import com.example.ais_store.DB.DbConnection;
import com.example.ais_store.TCP.ServerTCP;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/*
    Thick client with own db sends requests to server for checking something info
    for example if product exists for deleting or
    if category exists for adding product and so on...
 */

public class Main extends Application {
    ServerTCP server;

    public static void main(String[] args) {
        DbConnection.initialization("db");
        launch(args);
    }

    @FXML
    public void start(Stage stage) throws IOException {
        server = new ServerTCP(8888);
        new Thread(server).start();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
