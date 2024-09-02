package com.project.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.project.backend.dto.OrderDTO;
import com.project.backend.dto.PaymentResultDTO;
import com.project.backend.model.Order;
import com.project.backend.model.PaymentResult;
import com.project.backend.security.request.OrderRequest;
import com.project.backend.service.OrderService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("api/user/")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order/create")
    ResponseEntity<?> createOrder(@RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (null != userDetails) {

            String username = userDetails.getUsername();

            Order newOrder = orderService.createOrder(request, username);

            String orderID = Long.toString(newOrder.getOrderId());


            Map<String, String> data = new HashMap<>();
            data.put("orderId", orderID);

            
            return new ResponseEntity<>(data, HttpStatus.OK);
            
        } else {
            return new ResponseEntity<>("USER NOT FOUND", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/order/{orderId}")
    ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") String orderId) {

        OrderDTO response = orderService.getOrder(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
