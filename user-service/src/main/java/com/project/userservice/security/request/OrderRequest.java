package com.project.userservice.security.request;


import java.util.List;

import com.project.userservice.dto.AddressDto;
import com.project.userservice.dto.CouponDto;
import com.project.userservice.dto.CartProductDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {
    
    private String orderNumber;
    private List<CartProductDto> products;
    private AddressDto shippingAddress;    
    private String paymentMethod;
    private int total;
    private int totalBeforeDiscount;
    private String couponApplied;
}
