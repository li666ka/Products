package com.example.ais_store.DB;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnection {
    private static Connection CONNECTION;

    public static void initialization(String name) {
        try {
            String db_url = "jdbc:sqlite:" + name;
            CONNECTION = DriverManager.getConnection(db_url);

            //Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            CONNECTION = DriverManager.getConnection(db_url,config.toProperties());

            PreparedStatement st_create_category =
                    CONNECTION.prepareStatement(
                            "CREATE TABLE " +
                            "IF NOT EXISTS 'Category' (" +
                            "'id'   INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "'name' TEXT UNIQUE);");

            PreparedStatement st_create_product =
                    CONNECTION.prepareStatement(
                            "CREATE TABLE " +
                            "IF NOT EXISTS 'Product' (" +
                            "'id_product'   INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "'name'         TEXT UNIQUE NOT NULL, " +
                            "'id_category'  INTEGER     NOT NULL, " +
                            "'price'        DECIMAL     NOT NULL, " +
                            "'number'       INTEGER     NOT NULL, " +
                            "CONSTRAINT fk_category " +
                                    "FOREIGN KEY ('id_category') REFERENCES 'Category'('id') " +
                                    "ON UPDATE CASCADE " +
                                    "ON DELETE NO ACTION);");

            st_create_category.executeUpdate();
            st_create_product.executeUpdate();
        } catch (SQLException e){
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return CONNECTION;
    }
}
