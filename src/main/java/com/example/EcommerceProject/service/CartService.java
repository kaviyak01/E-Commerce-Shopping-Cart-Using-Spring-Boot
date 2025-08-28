package com.example.EcommerceProject.service;

import com.example.EcommerceProject.model.cart;

import java.util.List;

public interface CartService {

        public cart saveCart(int productId,int userId);

        public List<cart> getCartByUser(int userId);

        public int countCart(int userId);

        void updateCartQuantity(String sy, int cid);
}
