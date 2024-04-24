package com.group01.plantique.model;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    private String orderId;
    private String totalCost;

    private String subTotal;
    private String orderDate;
    private String orderStatus;
    private String paymentMethod;
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private String orderBy;
    private HashMap<String, Product> items = new HashMap<>();   // Assumed Product class exists and is properly structured.

    // No-argument constructor required for Firebase
    public Order() {}

    // Constructor with orderId
    public Order(String orderId) {
        this.orderId = orderId;
    }

    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getTotalCost() { return totalCost; }
    public void setTotalCost(String totalCost) { this.totalCost = totalCost; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOrderBy() { return orderBy; }
    public String getSubTotal() { return totalCost; }
    public void setSubTotal(String totalCost) { this.totalCost = totalCost; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
    public HashMap<String, Product> getItems() { return items; }
    public void setItems(HashMap<String, Product> items) { this.items = items; }
}
