package com.project.user-service.controller;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
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

import com.project.user-service.dto.PaymentResultDTO;

import com.project.user-service.security.request.PayRequest;
import com.project.user-service.service.OrderService;
import com.project.user-service.service.PaymentService;
//import com.project.user-service.service.RefundService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/pay/")
@RequiredArgsConstructor
public class PaymentController {

//    @Value("${imp_key}")
//    private String restApiKey;
//    @Value("${imp_secret}")
//    private String restApiSecret;

    //private IamportClient iamportClient;

    
    private final PaymentService paymentService;  
    //private final RefundService refundService;




    @PutMapping("/process")
    ResponseEntity<?> processPayment(@RequestBody PayRequest request, 
    @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        
        try {
            String username = userDetails.getUsername();
            return ResponseEntity.ok(paymentService.processPayment(username, request));

        }
        catch ( RuntimeException e) {
            
//            String token = refundService.getToken(restApiKey, restApiSecret);
//            refundService.refundRequest(token, request.getOrderNumber(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);           

        }
        
    }

    // @PostConstruct
    // public void init() {
    //     this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    // }

    //     @PostMapping("/verifyIamport/{imp_uid}")
    //     public IamportResponse<Payment> paymentByImpUid (@PathVariable("imp_uid") String imp_uid) throws
    //     IamportResponseException, IOException {
    //         return iamportClient.paymentByImpUid(imp_uid);
    //     }

}
