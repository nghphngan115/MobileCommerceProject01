package com.group01.plantique.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String productName;
    private String description;
    private int price;
    private int discount_price;
    private String imageurl;
    private String categoryId;
    private String discountNote;
    private String unit;
    private int stock;
    private int cartQuantity;

    public Product() {
        // Required empty constructor for Firebase
    }

    public Product(String productId, String productName, String description, int price, int discount_price, String imageurl, String categoryId, String discountNote, String unit, int stock) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discount_price = discount_price;
        this.imageurl = imageurl;
        this.categoryId = categoryId;
        this.discountNote = discountNote;
        this.unit = unit;
        this.stock = stock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(int discount_price) {
        this.discount_price = discount_price;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}