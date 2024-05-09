package com.group01.plantique.model;

public class ShippingAddress {
    private String addressId;
    private String fullName;
    private String address1;
    private String ward;
    private String district;
    private String province;
    private String phoneNumber;

    public ShippingAddress() {
        // Default constructor required for calls to DataSnapshot.getValue(Address.class)
    }

    public ShippingAddress(String addressId, String ward, String fullName, String address1, String district, String province, String phoneNumber) {
        this.addressId = addressId;
        this.fullName = fullName;
        this.address1 = address1;
        this.district = district;
        this.province = province;
        this.phoneNumber = phoneNumber;
        this.ward = ward;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    // Getters and setters
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return fullName + "\n" + address1 + ", " + district + ", " + province + "\nPhone: " + phoneNumber;
    }
}
