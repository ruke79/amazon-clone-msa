package com.project.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.repository.OrderRepository;
import com.project.backend.security.request.OrderRequest;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;


    public OrderDTO createORder(OrderRequest request) {

        
    }


}
