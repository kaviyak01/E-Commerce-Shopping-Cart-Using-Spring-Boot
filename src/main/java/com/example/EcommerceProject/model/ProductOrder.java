package com.example.EcommerceProject.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String orderId;

    private Date orderDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private double price;

    @ManyToOne
    private AppUser user;

    private String status;

    private String paymentType;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

    public ProductOrder() {
    }

    public ProductOrder(int id, String orderId, Date orderDate, Product product, int quantity, double price, AppUser user, String status, String paymentType,OrderAddress orderAddress) {
        this.id = id;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.user = user;
        this.status = status;
        this.paymentType = paymentType;
        this.orderAddress=orderAddress;
    }

    public OrderAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
