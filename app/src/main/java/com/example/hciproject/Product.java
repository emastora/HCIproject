package com.example.hciproject;

/**
 * Created by Schoox on 1/19/2017.
 */

public class Product {
    private String name;
    private double minPrice;
    private int numberStores;
    private String imageUrl;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public int getNumberStores() {
        return numberStores;
    }

    public void setNumberStores(int numberStores) {
        this.numberStores = numberStores;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
