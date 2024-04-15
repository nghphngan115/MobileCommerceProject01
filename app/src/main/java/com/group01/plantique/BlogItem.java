package com.group01.plantique;

public class BlogItem {
    private String title;
    private String image;
    private String content;

    public BlogItem(String title, String image, String content) {
        this.title = title;
        this.image = image;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }
}

