package com.example.EcommerceProject.model;

import jakarta.persistence.*;

@Entity
public class cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private Product product;

    private int quantity;

    @Transient
    private double totalPrice;

    private double totalOrderPrice;


    public cart() {
    }

    public cart(int id, AppUser user, Product product, int quantity, double totalPrice,double totalOrderPrice) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.totalOrderPrice=totalOrderPrice;
    }

    public double getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
