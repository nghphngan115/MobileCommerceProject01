package com.group01.plantique.model;

public class BlogItem {
    private String blogId;
    private String blogTitle;
    private String blogImage;
    private String blogContent;

    // Constructor
    public BlogItem() {
        // Default constructor required for calls to DataSnapshot.getValue(BlogItem.class)
    }

    public BlogItem(String blogId, String blogTitle, String blogImage, String blogContent) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogImage = blogImage;
        this.blogContent = blogContent;
    }

    // Getters and setters
    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return blogTitle;
    }

    public void setTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getImage() {
        return blogImage;
    }

    public void setImage(String blogImage) {
        this.blogImage = blogImage;
    }

    public String getContent() {
        return blogContent;
    }

    public void setContent(String blogContent) {
        this.blogContent = blogContent;
    }
}
