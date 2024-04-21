package com.group01.plantique.model;
//Category này để nạp test dô homescreen, nên sau này có cat chính thức thì có thể xóa
public class Category {
    String Catename;
    String imgUrl;

    public Category(String catename, String imgUrl) {
        Catename = catename;
        this.imgUrl = imgUrl;
    }

    Category()
    {
        
    }
    public String getCatename() {
        return Catename;
    }

    public void setCatename(String catename) {
        Catename = catename;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
