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
    private int stock; // Available items in inventory
    private int quantity; // Items in the user's cart
    private String discountNote;
    private String unit;

    // No-argument constructor required for Firebase
    public Product() {
    }

    // Constructor with all fields
    public Product(String productId, String productName, String description, int price, int discountPrice,
                   String imageUrl, String categoryId, int stock, String discountNote, String unit) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discount_price = discountPrice;
        this.imageurl = imageUrl;
        this.categoryId = categoryId;
        this.stock = stock;
        this.discountNote = discountNote;
        this.unit = unit;
        this.quantity = 0;
    }

    // Getters and setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    // Getter and setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public int getDiscountPrice() {
        return discount_price;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discount_price = discountPrice;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageurl = imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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
}
