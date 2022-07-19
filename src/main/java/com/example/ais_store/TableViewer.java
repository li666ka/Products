package com.example.ais_store;

import com.example.ais_store.Responses.Response;
import com.example.ais_store.DB.Categories;
import com.example.ais_store.DB.Products;
import com.example.ais_store.Models.Category;
import com.example.ais_store.Models.Product;
import com.example.ais_store.Packet.PacketInfo;
import com.example.ais_store.Requests.Command;
import com.example.ais_store.TCP.ClientTCP;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TableViewer implements Initializable {

    // region Fields
    private ClientTCP client;

    private final String allString = "Всі";

    @FXML
    private TableView<Product> tableProduct;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, Integer> pr_categoryIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Double> productPriceColumn;
    @FXML
    private TableColumn<Product, Integer> productNumberColumn;
    @FXML
    public TableColumn<Product, Double> productSumPriceColumn;

    @FXML
    public TextField pr_categoryIdField;
    @FXML
    public TextField productNameField;
    @FXML
    public TextField productPriceField;
    @FXML
    public TextField productNumberField;

    @FXML
    private TableView<Category> tableCategory;
    @FXML
    private TableColumn<Category, Integer> categoryIdColumn;
    @FXML
    private TableColumn<Category, String> categoryNameColumn;
    @FXML
    public TableColumn<Category, Double> categorySumPriceColumn;

    @FXML
    public ComboBox<String> categoriesComboBox;
    @FXML
    public TextField filteredId;

    @FXML
    public TextField categoryNameField;
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ClientTCP();
        try {
            client.startConnection(8888);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tableProduct.setOnKeyPressed(keyEvent -> {
            final Product selectedItem = tableProduct.getSelectionModel().getSelectedItem();

            if ( selectedItem != null )
            {
                if ( keyEvent.getCode().equals( KeyCode.DELETE ) )
                {
                    try {
                        int id = selectedItem.getIdProduct();
                        PacketInfo packetResponse = client.send(
                                Command.DELETE_PRODUCT_BY_ID.ordinal(),
                                String.valueOf(id));

                        int response = packetResponse.bMsg.cType;

                        if (response == Response.ERROR.ordinal()) {
                            String messageResponse = new String(packetResponse.bMsg.message);
                            throw new Exception(messageResponse);
                        } else if (response == Response.OK.ordinal()) {
                            Products.delete(id);
                        }
                        updateProducts();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });

        tableCategory.setOnKeyPressed(keyEvent -> {
            final Category selectedItem = tableCategory.getSelectionModel().getSelectedItem();

            if ( selectedItem != null )
            {
                if ( keyEvent.getCode().equals( KeyCode.DELETE ) )
                {
                    try {
                        int id = selectedItem.getId();
                        PacketInfo packetResponse = client.send(
                                Command.DELETE_CATEGORY_BY_ID.ordinal(),
                                String.valueOf(id));
                        int response = packetResponse.bMsg.cType;

                        if (response == Response.ERROR.ordinal()) {
                            String messageResponse = new String(packetResponse.bMsg.message);
                            throw new Exception(messageResponse);
                        } else if (response == Response.OK.ordinal()) {
                            Categories.delete(id);
                        }
                        updateCategories();
                        updateProducts();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });

        filteredId.setOnKeyPressed(keyEvent -> {
            if ( keyEvent.getCode().equals( KeyCode.ENTER ) )
            {
                try {
                    if (filteredId.getText().isEmpty() || filteredId.getText().isBlank()) {
                        updateProducts();
                    } else {
                        updateProducts(filteredId.getText());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        tableProduct.setEditable(true);
        tableCategory.setEditable(true);

        setupProductTableColumns();
        setupCategoryTableColumns();

        updateProducts();
        updateCategories();
        updateComboBoxCategories();

        categoriesComboBox.getSelectionModel().select(0);
    }

    public void addProductButtonClicked() {
        try {
            if (pr_categoryIdField.getText().isEmpty()
                    || productNameField.getText().isEmpty()
                    || productPriceField.getText().isEmpty()
                    || productNumberField.getText().isEmpty()) {
                throw new Exception("Input all values");
            }

            // data from fields
            int categoryId = Integer.parseInt(pr_categoryIdField.getText());
            String productName = productNameField.getText();
            double productPrice = Double.parseDouble(productPriceField.getText());
            int productNumber = Integer.parseInt(productNumberField.getText());

            PacketInfo responsePacket;
            responsePacket = client.send(
                    Command.ADD_PRODUCT.ordinal(),
                    productName + "$"
                            + categoryId + "$"
                            + productPrice + "$"
                            + productNumber);

            int response = responsePacket.bMsg.cType;

            if (response == Response.ERROR.ordinal()) {
                String messageResponse = new String(responsePacket.bMsg.message);
                throw new Exception(messageResponse);
            } else if (response == Response.OK.ordinal()) {
                Products.add(
                        categoryId,
                        productName,
                        productPrice,
                        productNumber);
                updateProducts();
                updateCategories();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void addCategoryButtonClicked() {
        try {
            if (categoryNameField.getText().isEmpty()) {
                throw new Exception("Input category name");
            }

            // data from fields
            String categoryName = categoryNameField.getText();
            PacketInfo responsePacket = client.send(Command.ADD_CATEGORY.ordinal(), categoryName);

            int response = responsePacket.bMsg.cType;

            if (response == Response.ERROR.ordinal()) {
                String messageResponse = new String(responsePacket.bMsg.message);
                throw new Exception(messageResponse);
            } else if (response == Response.OK.ordinal()) {
                Categories.add(categoryName);
                updateCategories();
                updateComboBoxCategories();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void selectedCategoryChanged() {
        String name = categoriesComboBox.getSelectionModel().getSelectedItem();
        if (name == null || name.isEmpty() || name.equals(allString)) {
            updateProducts();
        } else {
            updateProducts(Categories.getCategoryIdByName(name));
        }
    }

    private void setupProductTableColumns() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        productIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        productIdColumn.setOnEditCommit(event -> {
            try {
                int oldId = event.getRowValue().getIdProduct();
                int newId = event.getNewValue();
                PacketInfo responsePacket = client.send(
                        Command.UPDATE_PRODUCT_ID.ordinal(),
                        oldId + " " + newId);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Products.updateId(oldId, newId);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateProducts();
            }
        });

        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productNameColumn.setOnEditCommit(event -> {
            try {
                int id = event.getRowValue().getIdProduct();
                String newName = event.getNewValue();

                PacketInfo responsePacket = client.send(
                        Command.UPDATE_PRODUCT_NAME.ordinal(),
                        id + "$" + newName);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Products.updateName(id, newName);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateProducts();
            }
        });

        pr_categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("idCategory"));
        pr_categoryIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        pr_categoryIdColumn.setOnEditCommit(event -> {
            try {
                int productId = event.getRowValue().getIdProduct();
                int newCategoryId = event.getNewValue();

                PacketInfo responsePacket = client.send(
                        Command.UPDATE_PRODUCT_CATEGORY.ordinal(),
                        productId + " " + newCategoryId);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Products.updateCategoryId(productId, newCategoryId);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateProducts();
                updateCategories();
            }
        });

        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        productPriceColumn.setOnEditCommit(event -> {
            try {
                int id = event.getRowValue().getIdProduct();
                double newPrice = event.getNewValue();

                PacketInfo responsePacket = client.send(
                        Command.UPDATE_PRODUCT_PRICE.ordinal(),
                        id + " " + newPrice);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Products.updatePrice(id, newPrice);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateProducts();
                updateCategories();
            }
        });

        productNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        productNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        productNumberColumn.setOnEditCommit(event -> {
            try {
                int id = event.getRowValue().getIdProduct();
                int newNumber = event.getNewValue();
                PacketInfo responsePacket = client.send(
                        Command.UPDATE_PRODUCT_NUMBER.ordinal(),
                        id + " " + newNumber);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Products.updateNumber(id, newNumber);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateProducts();
                updateCategories();
            }
        });

        productSumPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sumPrice"));
    }

    private void setupCategoryTableColumns() {
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        categoryIdColumn.setOnEditCommit(event ->
        {
            try {
                int oldId = event.getRowValue().getId();
                int newId = event.getNewValue();

                PacketInfo responsePacket = client.send(
                        Command.UPDATE_CATEGORY_ID.ordinal(),
                        oldId + " " + newId);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Categories.updateId(oldId, newId);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateCategories();
                updateProducts();
                updateComboBoxCategories();
            }
        });

        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryNameColumn.setOnEditCommit(event ->
        {
            try {
                int id = event.getRowValue().getId();
                String newName = event.getNewValue();

                PacketInfo responsePacket = client.send(
                        Command.UPDATE_CATEGORY_NAME.ordinal(),
                        id + "$" + newName);

                int response = responsePacket.bMsg.cType;

                if (response == Response.ERROR.ordinal()) {
                    String messageResponse = new String(responsePacket.bMsg.message);
                    throw new Exception(messageResponse);
                } else if (response == Response.OK.ordinal()) {
                    Categories.updateName(id, newName);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } finally {
                updateCategories();
                updateComboBoxCategories();
            }
        });

        categorySumPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sumPrice"));
    }

    private void updateProducts() {
        ObservableList<Product> data = FXCollections.observableArrayList(
                Products.getAll());
        tableProduct.setItems(data);
    }

    private void updateProducts(int id_category) {
        ObservableList<Product> data = FXCollections.observableArrayList(Products.getAllFromCategory(id_category));
        tableProduct.setItems(data);
    }

    private void updateProducts(String id) {
        ObservableList<Product> data = FXCollections.observableArrayList(Products.getById(Integer.parseInt(id)));
        tableProduct.setItems(data);
    }

    private void updateCategories() {
        ObservableList<Category> data = FXCollections.observableArrayList(Categories.getAll());
        tableCategory.setItems(data);
    }

    private void updateComboBoxCategories() {
        ObservableList<String> data = FXCollections.observableArrayList(Categories.getCategoryNames());
        categoriesComboBox.setItems(data);
    }
}
