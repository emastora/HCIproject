package com.example.hciproject;

/**
 * Created by Schoox on 1/19/2017.
 */

public class Product {
    private String name;
    private int minPrice;
    private int numberStores;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
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
