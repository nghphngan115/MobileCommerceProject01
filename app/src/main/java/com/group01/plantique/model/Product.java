package com.group01.plantique.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String Productname;
    private String Price;
    private String imageUrl;

    // Constructor


    public Product(String productname, String price, String imageUrl) {
        Productname = productname;
        Price = price;
        this.imageUrl = imageUrl;
    }

    public String getProductname() {
        return Productname;
    }

    public void setProductname(String productname) {
        Productname = productname;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

    // Getters


