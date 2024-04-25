package com.group01.plantique.model;

public class BlogItem {
    private String blogId; // Thêm trường blogId
    private String blogTitle;
    private String blogImage;
    private String blogContent;

    public BlogItem(String blogId, String title, String image, String content) {
        this.blogId = blogId;
        this.blogTitle = title;
        this.blogImage = image;
        this.blogContent = content;
    }

    public String getBlogId() {
        return blogId;
    }

    public String getTitle() {
        return blogTitle;
    }

    public String getImage() {
        return blogImage;
    }

    public String getContent() {
        return blogContent;
    }
}
