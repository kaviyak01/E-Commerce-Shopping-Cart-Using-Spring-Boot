package com.example.EcommerceProject.ServiceImpl;

import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.model.Product;
import com.example.EcommerceProject.model.cart;
import com.example.EcommerceProject.repository.CartRepos;
import com.example.EcommerceProject.repository.ProductRepos;
import com.example.EcommerceProject.repository.UserRepos;
import com.example.EcommerceProject.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepos cartRepos;

    @Autowired
    private UserRepos userRepos;

    @Autowired
    private ProductRepos productRepos;


    @Override
    public cart saveCart(int productId, int userId) {

        AppUser user=userRepos.findById(userId).get();
        Product product=productRepos.findById(productId).get();

        cart cartStatus=cartRepos.findByProductIdAndUserId(productId,userId);

        cart cart=null;

        if(ObjectUtils.isEmpty(cartStatus))
        {
            cart=new cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1*product.getDiscountPrice());
        }
        else{
          cart=cartStatus;
          cart.setQuantity(cart.getQuantity()+1);
          cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
        }

        cart saveCart=cartRepos.save(cart);
        return saveCart;
    }

    @Override
    public List<cart> getCartByUser(int userId) {

       List<cart> carts=cartRepos.findByUserId(userId);

       Double totalOrderPrice=0.0;
       List<cart> updateCarts=new ArrayList<>();

       for(cart c: carts)
       {
           double totalPrice = (c.getQuantity()*c.getProduct().getDiscountPrice());
           c.setTotalPrice(totalPrice);

           totalOrderPrice =totalOrderPrice+totalPrice;
           c.setTotalOrderPrice(totalOrderPrice);
           updateCarts.add(c);
       }


        return carts;
    }

    @Override
    public int countCart(int userId) {
        int count=cartRepos.countByUserId(userId);
        return count;
    }

    @Override
    public void updateCartQuantity(String sy, int cid) {
        cart cart=cartRepos.findById(cid).get();
        int updateQuantity;
        if(sy.equalsIgnoreCase("de"))
        {
           updateQuantity= cart.getQuantity()-1;
           if(updateQuantity<=0)
           {
               cartRepos.delete(cart);
           }else {
               cart.setQuantity(updateQuantity);
               cartRepos.save(cart);
           }

        }
        else {
            updateQuantity=cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            cartRepos.save(cart);
        }

    }
}
