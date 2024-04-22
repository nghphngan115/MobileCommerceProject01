package com.group01.plantique.model;

public class Product {
    private String id;
    private String name;
    private String imageurl;
    private int price;
    private int discount_price;

    public Product() {
        // Required empty public constructor
    }

    public Product(String id, String name, String imageurl, int price, int discount_price) {
        this.id = id;
        this.name = name;
        this.imageurl = imageurl;
        this.price = price;
        this.discount_price = discount_price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public int getPrice() {
        return price;
    }

    public int getDiscount_price() {
        return discount_price;
    }
}
