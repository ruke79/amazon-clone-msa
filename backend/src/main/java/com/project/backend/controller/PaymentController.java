package com.project.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dto.PaymentResultDTO;
import com.project.backend.security.request.PayRequest;
import com.project.backend.service.OrderService;
import com.project.backend.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/pay/")
public class PaymentController {

    @Value("${imp.key}")
    private String restApiKey;
    @Value("${imp.secret}")
    private String restApiSecret;

    private IamportClient iamportClient;

    @Autowired
    PaymentService paymentService;


    @PutMapping("/process")
    ResponseEntity<PaymentResultDTO> processPayment(@RequestBody PayRequest request, 
    @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String username = userDetails.getUsername();
            return ResponseEntity.ok(paymentService.processPayment(username, request));

        }
        catch ( Exception e) {

            log.error("Error creating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
        
    }

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }

        @PostMapping("/verifyIamport/{imp_uid}")
        public IamportResponse<Payment> paymentByImpUid (@PathVariable("imp_uid") String imp_uid) throws
        IamportResponseException, IOException {
            return iamportClient.paymentByImpUid(imp_uid);
        }

}
