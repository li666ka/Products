package com.example.ais_store.DB;

import com.example.ais_store.Models.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Products {
    public static ArrayList<Product> getAll() {
        var products = new ArrayList<Product>();

        try {
            Statement st = DbConnection.getConnection().createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Product");

            while (res.next()) {
                int id_product = res.getInt("id_product");
                String name = res.getString("name");
                int id_category = res.getInt("id_category");
                double price = res.getDouble("price");
                int product_number = res.getInt("number");
                products.add(new Product(
                        id_product,
                        id_category,
                        name,
                        price,
                        product_number,
                        price*product_number));
            }
            res.close();
            st.close();
        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return products;
    }

    public static ArrayList<Product> getAllFromCategory(int id) {
        var products = new ArrayList<com.example.ais_store.Models.Product>();

        try {
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT * FROM Product WHERE id_category = (?)");
            st.setInt(1, id);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                int id_product = res.getInt("id_product");
                int id_category = res.getInt("id_category");
                String name = res.getString("name");
                double price = res.getDouble("price");
                int product_number = res.getInt("number");
                com.example.ais_store.Models.Product product = new Product(id_product, id_category, name, price, product_number,
                        price*product_number);
                products.add(product);
            }
            res.close();
            st.close();

        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return products;
    }

    public static Product getById(int id) {
        try{
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT * FROM Product WHERE id_product=(?)");
            st.setInt(1, id);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                int id_product = res.getInt("id_product");
                String name = res.getString("name");
                int id_category = res.getInt("id_category");
                double price = res.getDouble("price");
                int product_number = res.getInt("number");

                return new Product(id_product, id_category, name, price, product_number, price * product_number);
            }
            res.close();
            st.close();
        } catch(SQLException e){
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return null;
    }

    public static Product getByName(String name) {
        try{
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT * FROM Product WHERE name=(?)");
            st.setString(1, name);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                int id_product = res.getInt("id_product");
                int id_category = res.getInt("id_category");
                double price = res.getDouble("price");
                int product_number = res.getInt("number");

                return new Product(id_product, id_category, name, price, product_number, price * product_number);
            }
            res.close();
            st.close();
        } catch(SQLException e){
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return null;
    }

    public static void add(int id_category, String name, double price, int product_number) throws SQLException {

        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "INSERT INTO Product (id_category, name, price, number) " +
                "VALUES ((?), (?), (?), (?))");
        statement.setInt(1, id_category);
        statement.setString(2, name);
        statement.setDouble(3, price);
        statement.setInt(4, product_number);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updateId(int old_id_product, int new_id_product) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Product SET id_product=(?) WHERE id_product=(?)");
        statement.setDouble(1, new_id_product);
        statement.setInt(2, old_id_product);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updateName(int id_product, String newName) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Product SET name=(?) WHERE id_product=(?)");
        statement.setString(1, newName);
        statement.setInt(2, id_product);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updatePrice(int id_product, double newPrice) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Product SET price=(?) WHERE id_product=(?)");
        statement.setDouble(1, newPrice);
        statement.setInt(2, id_product);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updateNumber(int id_product, int new_product_number) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Product SET number=(?) WHERE id_product=(?)");
        statement.setDouble(1, new_product_number);
        statement.setInt(2, id_product);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updateCategoryId(int id_product, int new_id_category) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Product SET id_category=(?) WHERE id_product=(?)");
        statement.setDouble(1, new_id_category);
        statement.setInt(2, id_product);

        final boolean oldAutoCommit = statement.getConnection().getAutoCommit();
        statement.getConnection().setAutoCommit(false);

        try {
            statement.executeUpdate();
        } catch(Exception e) {
            statement.getConnection().rollback();
        } finally {
            statement.getConnection().commit();
            statement.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void delete(int id) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "DELETE FROM Product WHERE id_product=(?)");
        statement.setInt(1, id);
        statement.execute();
        statement.close();
    }
}
