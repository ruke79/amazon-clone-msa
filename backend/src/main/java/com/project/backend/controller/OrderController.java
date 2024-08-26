package com.project.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import net.minidev.json.JSONObject;

@Slf4j
@RestController
@RequestMapping("api/user/")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order/create")
    ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequest request) {

        Order newOrder = orderService.createOrder(request);

        String orderID = Long.toString(newOrder.getOrderId());

        log.info(orderID);

        Map<String, String> data = new HashMap<>();
        data.put("orderId", orderID);

        

        if (null != newOrder) {
            return new ResponseEntity<>(data, HttpStatus.OK);
        }

        return null;
    }

    @PostMapping("/order/payment")
    ResponseEntity<Boolean> processPayment(@RequestParam("orderId") String orderId) {

        Boolean result = Boolean.valueOf(orderService.processPayment(orderId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") String orderId) {

        OrderDTO response = orderService.getOrder(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
