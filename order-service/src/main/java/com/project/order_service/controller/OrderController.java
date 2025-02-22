package com.project.order_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.order_service.dto.OrderDto;
import com.project.order_service.dto.request.OrderRequest;
import com.project.order_service.dto.response.MessageResponse;
import com.project.order_service.model.Order;
import com.project.order_service.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/order/")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {

        try {

            Order newOrder = orderService.createOrder(request);

            String orderID = Long.toString(newOrder.getOrderId());

            Map<String, String> data = new HashMap<>();
            data.put("orderId", orderID);

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }

    }

    @GetMapping("/{orderId}")
    ResponseEntity<?> getOrder(@PathVariable("orderId") String orderId) {

        try {
            OrderDto response = orderService.getOrder(Long.parseLong(orderId), "");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

}
