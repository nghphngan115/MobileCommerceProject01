package com.group01.plantique.model;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    private String orderId;
    private String totalCost;  // Changed from String to int to match totalCost handling
    private long shippingFee;    // Proper camelCase naming
    private String subTotal;   // Changed from String to int
    private String orderDate;
    private String orderStatus;
    private String paymentMethod;
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private String orderBy;
    private HashMap<String, Product> items = new HashMap<>();

    // No-argument constructor required for Firebase
    public Order() {}

    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTotalCost() { return totalCost; }
    public void setTotalCost(String totalCost) { this.totalCost = totalCost; }

    public long getShippingFee() { return shippingFee; }
    public void setShippingFee(long shippingFee) { this.shippingFee = shippingFee; }

    public String getSubTotal() { return subTotal; }
    public void setSubTotal(String subTotal) { this.subTotal = subTotal; }

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
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }

    public HashMap<String, Product> getItems() { return items; }
    public void setItems(HashMap<String, Product> items) { this.items = items; }
}
