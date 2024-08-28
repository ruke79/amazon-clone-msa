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
import com.project.backend.service.RefundService;
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

    @Value("${imp_key}")
    private String restApiKey;
    @Value("${imp_secret}")
    private String restApiSecret;

    private IamportClient iamportClient;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RefundService refundService;


    @PutMapping("/process")
    ResponseEntity<PaymentResultDTO> processPayment(@RequestBody PayRequest request, 
    @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String username = userDetails.getUsername();
            return ResponseEntity.ok(paymentService.processPayment(username, request));

        }
        catch ( RuntimeException e) {

            log.info("주문 상품 환불 진행 : 주문 번호 {}", request.getOrderId());
            String token = refundService.getToken(restApiKey, restApiSecret);
            refundService.refundRequest(token, request.get, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            

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
