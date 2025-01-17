package com.ledger.SalesHelper;

public class SalesModel {

    String name;
    String id;

    String email;
    String phone;
    String address;
    String image;

    String salesTarget, collectionTarget;

    public SalesModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSalesTarget() {
        return salesTarget;
    }

    public void setSalesTarget(String salesTarget) {
        this.salesTarget = salesTarget;
    }

    public String getCollectionTarget() {
        return collectionTarget;
    }

    public void setCollectionTarget(String collectionTarget) {
        this.collectionTarget = collectionTarget;
    }
}
