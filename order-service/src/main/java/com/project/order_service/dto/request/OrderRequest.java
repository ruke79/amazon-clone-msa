package com.project.order_service.dto.request;


import java.util.List;

import com.project.order_service.dto.OrderAddressDto;
//import com.project.cart_service.dto.CouponDto;
import com.project.order_service.dto.CartProductDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequest {
    
    private String orderNumber;
    private List<CartProductDto> products;
    private OrderAddressDto shippingAddress;        
    private String paymentMethod;
    private int total;
    private int totalBeforeDiscount;
    private String couponApplied;
    private String userId;
}
