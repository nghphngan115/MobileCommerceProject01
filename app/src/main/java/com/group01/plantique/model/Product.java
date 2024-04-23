package com.group01.plantique.model;

public class Product {
    private String productId;
    private String productName;
    private String description;
    private int price;
    private int discount_price;
    private String imageurl;
    private String categoryId;
    private int quantity; // Thêm trường quantity

    public Product() {
        // Required empty constructor for Firebase
    }

    public Product(String productId, String productName, String description, int price, int discount_price, String imageurl, String categoryId, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discount_price = discount_price;
        this.imageurl = imageurl;
        this.categoryId = categoryId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
