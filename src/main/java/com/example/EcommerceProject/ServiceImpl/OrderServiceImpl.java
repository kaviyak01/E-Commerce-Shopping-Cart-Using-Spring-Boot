package com.example.EcommerceProject.ServiceImpl;

import com.example.EcommerceProject.model.*;
import com.example.EcommerceProject.repository.CartRepos;
import com.example.EcommerceProject.repository.ProductOrderRepos;
import com.example.EcommerceProject.service.OrderService;
import com.example.EcommerceProject.util.CommonUtil;
import com.example.EcommerceProject.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepos productOrderRepos;

    @Autowired
    private CartRepos cartRepos;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveProduct(int userid, OrderRequest orderRequest) {

        List<cart> carts=cartRepos.findByUserId(userid);

        for(cart c: carts)
        {
            ProductOrder order=new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(c.getProduct());
            order.setPrice(c.getProduct().getDiscountPrice());

            order.setQuantity(c.getQuantity());
            order.setUser(c.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.name());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address=new OrderAddress();

            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNumber(orderRequest.getMobileNumber());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            ProductOrder saveOrder=productOrderRepos.save(order);

            commonUtil.sendMailforOrderStatus(saveOrder,"Success");
        }
    }

    @Override
    public List<ProductOrder> viewOrders(int userId) {
        List<ProductOrder> orders=productOrderRepos.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder updateStatusByid(int oid, String status) {

        Optional<ProductOrder> orders=productOrderRepos.findById(oid);

        if(orders.isPresent())
        {
            ProductOrder order=productOrderRepos.findById(oid).get();
            order.setStatus(status);
            ProductOrder saveStatus=productOrderRepos.save(order);
            return  saveStatus;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepos.findAll();
    }

    @Override
    public ProductOrder getSearchOrder(String oid) {
        return productOrderRepos.findByOrderId(oid);
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        return productOrderRepos.findAll(pageable);
    }

    @Override
    public Page<ProductOrder> getSearchOrder(String oid,int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        return productOrderRepos.findByOrderId(oid,pageable);
    }


}
