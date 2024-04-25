package com.group01.plantique.model;

public class BlogItem {
    private String blogId; // Thêm trường blogId
    private String title;
    private String image;
    private String content;

    public BlogItem(String blogId, String title, String image, String content) {
        this.blogId = blogId;
        this.title = title;
        this.image = image;
        this.content = content;
    }

    public String getBlogId() {
        return blogId;
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
