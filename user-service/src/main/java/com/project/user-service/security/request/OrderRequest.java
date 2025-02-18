package com.project.user-service.security.request;


import java.util.List;

import com.project.user-service.dto.AddressDTO;
import com.project.user-service.dto.CouponDTO;
import com.project.user-service.dto.CartProductDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {
    
    private String orderNumber;
    private List<CartProductDTO> products;
    private AddressDTO shippingAddress;    
    private String paymentMethod;
    private int total;
    private int totalBeforeDiscount;
    private String couponApplied;
}
