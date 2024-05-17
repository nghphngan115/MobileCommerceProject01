package com.group01.plantique.model;

public class HighlightBlogItem {
    private String blogId;
    private String blogTitle;
    private String blogContent;
    private String blogImage;
    private String blogAuthor;
    private String blogDate;

    // Default constructor required for Firebase
    public HighlightBlogItem() {
    }

    // Constructor with parameters
    public HighlightBlogItem(String blogId, String blogTitle, String blogContent, String blogImage, String blogAuthor, String blogDate) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogContent = blogContent;
        this.blogImage = blogImage;
        this.blogAuthor = blogAuthor; // Thay author thành blogAuthor
        this.blogDate = blogDate; // Thay date thành blogDate
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

    public String getBlogAuthor() {
        return blogAuthor;
    }

    public void setBlogAuthor(String blogAuthor) {
        this.blogAuthor = blogAuthor;
    }

    public String getBlogDate() {
        return blogDate;
    }

    public void setBlogDate(String blogDate) {
        this.blogDate = blogDate;
    }
}
