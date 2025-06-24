package com.example.android_lab.models;

import java.io.Serializable;

public class Food  implements Serializable, CartItem {
    private String id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;
    private boolean isPopular;
    private int quantity = 1;

    public Food(String foodId, String name, double price, boolean b, String description, int quantityStr) {
        this.id = foodId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = "";
        this.isPopular = b;
        this.quantity = quantityStr;
    }

    public String getDescription() {
        return description;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public Food() {}

    public Food(String id, String name, double price, String imageUrl, boolean isPopular, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
        this.quantity = quantity;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isPopular() {
        return isPopular;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }
}
