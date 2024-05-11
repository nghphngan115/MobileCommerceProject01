package com.group01.plantique.model;

public class Review {
    private String userId;
    private double rating;
    private String review;
    private long timestamp;
    private String userName;

    public Review() {
        // Constructor mặc định rỗng cần thiết cho Firebase
    }

    // Constructor mới để khởi tạo các trường dữ liệu
    public Review(String userId, double rating, String review, long timestamp) {
        this.userId = userId;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}