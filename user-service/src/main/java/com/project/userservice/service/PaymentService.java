package com.project.userservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.userservice.dto.PaymentResultDto;
import com.project.userservice.model.Order;
import com.project.userservice.model.PaymentResult;
import com.project.userservice.repository.OrderRepository;
import com.project.userservice.repository.PaymentRepository;
import com.project.userservice.security.request.PayRequest;

@Service
public class PaymentService {

    
    private final PaymentRepository paymentRepository;
    
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    
    

    public PaymentResultDto processPayment(String userName, PayRequest request) {

             
        try {
            Order order = orderRepository.findByOrderNumber(request.getOrderNumber())
            .orElseThrow(() -> new RuntimeException("Order not found"));

            orderRepository.updateIsPaidByOrderNumber(request.getOrderNumber(), true);       

            PaymentResult pay = PaymentResult.builder()
            //.payPrice(request.getPayPrice())
            .payStatus(request.getPayStatus())
            //.payType(PayType.C)
            .payDateTime(request.getPayDateTime())
            .payCancelDateTime(request.getPayCancelDateTime())
            .build();

            order.setPaymentResult(pay);            

            paymentRepository.save(pay);            

            PaymentResultDto result = PaymentResultDto.builder()
            .paymentId(Long.toString(pay.getPaymentId()))
            //.payPrice(pay.getPayPrice())
            .payStatus(pay.getPayStatus())
            //.payType(pay.getPayType())
            .payDateTime(pay.getPayDateTime())
            .payCancelDateTime(pay.getPayCancelDateTime())
            .build();

            return result;
        
           
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment");
        }       
        
    }

    

}
