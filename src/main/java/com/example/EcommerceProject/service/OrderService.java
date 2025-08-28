package com.example.EcommerceProject.service;

import com.example.EcommerceProject.model.OrderRequest;
import com.example.EcommerceProject.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    public void saveProduct(int userid, OrderRequest orderRequest);

    public List<ProductOrder> viewOrders(int userId);

    public ProductOrder updateStatusByid(int oid, String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getSearchOrder(String oid);

    public Page<ProductOrder> getAllOrdersPagination(int pageNo,int pageSize);

    public Page<ProductOrder> getSearchOrder(String oid,int pageNo,int pageSize);


}
