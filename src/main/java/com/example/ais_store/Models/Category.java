package com.example.ais_store.Models;

import com.example.ais_store.DB.Categories;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Category {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleDoubleProperty sumPrice;

    public Category(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.sumPrice = new SimpleDoubleProperty(Categories.getTotalSumOfProductsInCategory(id));
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getSumPrice() {
        return sumPrice.get();
    }

    public void setSumPrice(double sumPrice) {
        this.sumPrice.set(sumPrice);
    }
}
