package com.group01.plantique.model;
public class Category{
    private String id;
    private String name;
    private String imageurl;

    public Category() {
        // Required empty public constructor
    }

    public Category(String id, String name, String imageurl) {
        this.id = id;
        this.name = name;
        this.imageurl = imageurl;
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
}
