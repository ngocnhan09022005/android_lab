package com.example.android_lab.models;

import java.io.Serializable;

public class Food  implements Serializable {
    private String id;
    private String name;
    private double price;
    private String imageUrl;

    public String getDescription() {
        return description;
    }

    private String description;
    private boolean isPopular;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity;

    public Food() {}

    public Food(String id, String name, double price, String imageUrl, boolean isPopular) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
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
