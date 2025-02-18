package com.project.user-service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.user-service.dto.PaymentResultDTO;
import com.project.user-service.model.Order;
import com.project.user-service.model.PaymentResult;
import com.project.user-service.repository.OrderRepository;
import com.project.user-service.repository.PaymentRepository;
import com.project.user-service.security.request.PayRequest;

@Service
public class PaymentService {

    
    private final PaymentRepository paymentRepository;
    
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public PaymentResultDTO processPayment(String userName, PayRequest request) {

             
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

            PaymentResultDTO result = PaymentResultDTO.builder()
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
