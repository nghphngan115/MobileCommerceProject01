package com.group01.plantique.model;

public class HighlightBlogItem {
    private String blogId;
    private String blogTitle;
    private String blogImage;
    private String blogContent;

    // Constructor
    public HighlightBlogItem() {
        // Default constructor required for calls to DataSnapshot.getValue(BlogItem.class)
    }

    public HighlightBlogItem(String blogId, String blogTitle, String blogImage, String blogContent) {
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

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }
}
