package com.group01.plantique.model;

public class NotificationApp {
    private String title;
    private String content;
    private int imageResId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String type; // e.g., "order_confirmation", "promotion", etc.
    private String orderId;
    public NotificationApp(String title, String content, int imageResId) {
        this.title = title;
        this.content = content;
        this.imageResId = imageResId;
        this.type = type;
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
    public int getImageResId() {
        return imageResId;
    }
}
