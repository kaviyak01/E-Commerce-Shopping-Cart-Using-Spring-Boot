package com.example.EcommerceProject.util;

public enum OrderStatus {

    IN_PROGRESS(1,"In Progress"),
    ORDER_RECEIVED(2,"Order Received"),
    PRODUCT_PACKED(3,"Product Packed"),
    OUT_FOR_DELIVERY(4,"Out for Delivery"),
    DELIVERED(5,"Delivered"),
    CANCELLED(6,"Cancelled"),
    SUCCESS(7,"Success");

    private int id;

    private String name;

    OrderStatus() {
    }

    OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
