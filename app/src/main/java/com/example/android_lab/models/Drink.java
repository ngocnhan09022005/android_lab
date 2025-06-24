package com.example.android_lab.models;

import java.io.Serializable;

public class Drink implements CartItem, Serializable {
    private String id;
    private String name;
    private double price;
    private String imageUrl;
    private String description;
    private boolean isPopular;
    private int quantity = 1;

    public Drink() {}

    public Drink(String id, String name, double price, String imageUrl, String description, boolean isPopular, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isPopular = isPopular;
        this.quantity = quantity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public boolean isPopular() { return isPopular; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setPopular(boolean popular) { isPopular = popular; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
