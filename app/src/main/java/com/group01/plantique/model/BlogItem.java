package com.group01.plantique.model;

public class BlogItem {
    private String blogId;
    private String blogTitle;
    private String blogContent;
    private String blogImage;

    // Default constructor required for Firebase
    public BlogItem() {
    }

    // Constructor with parameters
    public BlogItem(String blogId, String blogTitle, String blogContent, String blogImage) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogContent = blogContent;
        this.blogImage = blogImage;
    }

    // Getters and setters
    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }
}
