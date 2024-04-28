package com.group01.plantique.model;

public class ModelReview {
    private String userId;
    private double ratings;
    private String review;
    private long timestamp;

    public ModelReview(String userId, double ratings, String review, long timestamp) {
        this.userId = userId;
        this.ratings = ratings;
        this.review = review;
        this.timestamp = timestamp;
    }


    // Getter và Setter cho các trường
    // ...

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
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
}