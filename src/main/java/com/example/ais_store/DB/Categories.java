package com.example.ais_store.DB;

import com.example.ais_store.Models.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Categories {
    public static ArrayList<Category> getAll() {
        var categories = new ArrayList<Category>();

        try {
            Statement st = DbConnection.getConnection().createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Category");

            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                categories.add(new Category(id, name));
            }
            res.close();
            st.close();
        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return categories;
    }

    public static void add(String name) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection()
                .prepareStatement("INSERT INTO Category (name) VALUES (?)");
        statement.setString(1, name);
        statement.executeUpdate();

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

    public static void updateId(int old_category_id, int new_category_id) throws SQLException {
        PreparedStatement updateCategory = DbConnection.getConnection().prepareStatement(
                "UPDATE Category SET id=(?) WHERE id=(?)");
        updateCategory.setInt(1, new_category_id);
        updateCategory.setInt(2, old_category_id);

        final boolean oldAutoCommit = updateCategory.getConnection().getAutoCommit();

        updateCategory.getConnection().setAutoCommit(false);

        try {
            updateCategory.executeUpdate();
        } catch(Exception e) {
            updateCategory.getConnection().rollback();
        } finally {
            updateCategory.getConnection().commit();
            updateCategory.getConnection().setAutoCommit(oldAutoCommit);
        }
    }

    public static void updateName(int id_category, String newName) throws SQLException {
        PreparedStatement statement = DbConnection.getConnection().prepareStatement(
                "UPDATE Category SET name=(?) WHERE id=(?)");
        statement.setString(1, newName);
        statement.setInt(2, id_category);

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

    public static Category getById(int id) {
        try {
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT * FROM Category WHERE id=(?)");
            st.setInt(1, id);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                int id_category = res.getInt("id");
                String name = res.getString("name");
                return new com.example.ais_store.Models.Category(id_category, name);
            }

            res.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getCategoryNames() {
        var categoryNames = new ArrayList<String>();
        categoryNames.add("Всі");
        try {
            PreparedStatement st = DbConnection.getConnection().prepareStatement("SELECT name FROM Category");
            ResultSet res = st.executeQuery();

            while (res.next()) {
                String name = res.getString("name");
                categoryNames.add(name);
            }
            res.close();
            st.close();
        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return categoryNames;
    }

    public static int getCategoryIdByName(String name) {
        try {
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT id FROM Category WHERE name=(?)");
            st.setString(1, name);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                return res.getInt("id");
            }
            res.close();
            st.close();
        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return -1;
    }

    public static double getTotalSumOfProductsInCategory(int id) {
        try {
            PreparedStatement st = DbConnection.getConnection().prepareStatement(
                    "SELECT SUM(price * number) AS total_sum " +
                    "FROM Product " +
                    "WHERE id_category=(?)");
            st.setInt(1, id);
            ResultSet res = st.executeQuery();

            while (res.next()) {
                return res.getInt("total_sum");
            }
            res.close();
            st.close();
        } catch(SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }

        return 0;
    }

    public static void delete(int id) throws SQLException {
        PreparedStatement deleteAllProductsOfCategory = DbConnection.getConnection().prepareStatement(
                "DELETE FROM Product " +
                "WHERE id_category=(?)");
        PreparedStatement deleteCategory = DbConnection.getConnection().prepareStatement(
                "DELETE FROM Category " +
                "WHERE id=(?)");
        deleteAllProductsOfCategory.setInt(1, id);
        deleteCategory.setInt(1, id);

        deleteAllProductsOfCategory.execute();
        deleteCategory.execute();

        deleteAllProductsOfCategory.close();
        deleteCategory.close();
    }
}
