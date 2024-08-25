package com.project.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.security.request.OrderRequest;

@RestController
@RequestMapping("/user/order")
public class OrderController {

    @PostMapping("/create")
    ReponseEntity<> createOrder(@RequestBody OrderRequest request) {

        
    }

}
