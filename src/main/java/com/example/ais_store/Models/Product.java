package com.example.ais_store.Models;

import javafx.beans.property.*;

public class Product {
    private SimpleIntegerProperty idProduct;
    private SimpleIntegerProperty idCategory;
    private SimpleStringProperty name;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty number;
    private SimpleDoubleProperty sumPrice;

    public Product(int id_product, int id_category, String name, double price, int number, double sumPrice) {
        this.idProduct = new SimpleIntegerProperty(id_product);
        this.idCategory = new SimpleIntegerProperty(id_category);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.number = new SimpleIntegerProperty(number);
        this.sumPrice = new SimpleDoubleProperty(sumPrice);
    }

    public final int getIdProduct() {
        return idProduct.get();
    }

    public final void setIdProduct(int id_product) {
        this.idProduct.set(id_product);
    }

    public final int getIdCategory() {
        return idCategory.get();
    }

    public final void setIdCategory(int id_category) {
        this.idProduct.set(id_category);
    }

    public final String getName() {
        return name.get();
    }

    public final void setName(String nameProduct) {
        this.name.set(nameProduct);
    }

    public final double getPrice() {
        return price.get();
    }

    public final void setPrice(double price) {
        this.price.set(price);
    }

    public final int getNumber() {
        return number.get();
    }

    public final void setNumber(int productNumber) { this.number.set(productNumber); }

    public final double getSumPrice() {
        return sumPrice.get();
    }

    public final void setSumPrice(double sumTotal) {
        this.sumPrice.set(sumTotal);
    }
}
