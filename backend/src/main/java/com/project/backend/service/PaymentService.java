package com.project.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.constants.PayType;
import com.project.backend.dto.PaymentResultDTO;
import com.project.backend.model.Order;
import com.project.backend.model.PaymentResult;
import com.project.backend.repository.OrderRepository;
import com.project.backend.repository.PaymentRepository;
import com.project.backend.security.request.PayRequest;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    public PaymentResultDTO processPayment(String userName, PayRequest request) {

        Long orderId = Long.parseLong(request.getOrderId());
     
        try {
            Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

            PaymentResult pay = PaymentResult.builder()
            .payPrice(request.getPayPrice())
            .payStatus(request.getPayStatus())
            .payType(PayType.C)
            .payDateTime(request.getPayDateTime())
            .payCancelDateTime(request.getPayCancelDateTime())
            .build();

            order.setPaymentResult(pay);            

            paymentRepository.save(pay);            

            PaymentResultDTO result = PaymentResultDTO.builder()
            .paymentId(Long.toString(pay.getPaymentId()))
            .payPrice(pay.getPayPrice())
            .payStatus(pay.getPayStatus())
            .payType(pay.getPayType())
            .payDateTime(pay.getPayDateTime())
            .payCancelDateTime(pay.getPayCancelDateTime())
            .build();

            return result;
        //      int updatedRowcount = orderRepository.updateIsPaidById(orderId, true);       

        // if (updatedRowcount > 0) {
        //     return true;
        // }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }       
        
    }


}
