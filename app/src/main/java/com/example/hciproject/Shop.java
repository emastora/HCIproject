package com.example.hciproject;

/**
 * Created by Schoox on 1/20/2017.
 */

public class Shop {
    private String name;
    private int shopId;
    private double price;
    private boolean immediate;
    private boolean lat;
    private boolean aLong;
    private String availabilityString;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public boolean getLat() {
        return lat;
    }

    public boolean isLat() {
        return lat;
    }

    public void setLat(boolean lat) {
        this.lat = lat;
    }

    public boolean getLong() {
        return aLong;
    }

    public boolean isaLong() {
        return aLong;
    }

    public void setaLong(boolean aLong) {
        this.aLong = aLong;
    }

    public String getAvailabilityString() {
        return availabilityString;
    }

    public void setAvailabilityString(String availabilityString) {
        this.availabilityString = availabilityString;
    }
}
