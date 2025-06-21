package com.example.android_lab.data.model;

public class Food {
    private String id;
    private String name;

    public void setPrice(double price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    private double price;
    private String imageUrl;

    public boolean isPopular() {
        return isPopular;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    private boolean isPopular;

    public Food() {} // Needed for Firebase

    public Food(String id, String name, double price, String imageUrl, boolean isPopular) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
    }


}
