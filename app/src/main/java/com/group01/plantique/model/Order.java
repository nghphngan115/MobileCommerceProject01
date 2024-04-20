package com.group01.plantique.model;

public class Order {
    private String orderId;
    private String orderDate;
    private int quantity;
    private double totalPrice;
    private String status;
    private String finishedDate;

    // Constructor
    public Order(String orderId, String orderDate, int quantity, double totalPrice, String status, String finishedDate) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.finishedDate = finishedDate;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }
}

