package com.group01.plantique.model;


public class Category {
    private String cateId;
    private String cateName;
    private String imageurl;


    public Category() {
        // Required empty constructor for Firebase
    }

    public Category(String cateId, String cateName, String imageurl) {
        this.cateId = cateId;
        this.cateName = cateName;
        this.imageurl = imageurl;

    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


}
