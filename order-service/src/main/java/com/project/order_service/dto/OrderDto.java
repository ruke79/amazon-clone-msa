package com.project.order_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.common.dto.SharedUserDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class OrderDto {

    private String id;

    private String trackingId;
    
    private List<OrderedProductDto> products;

    private OrderAddressDto shippingAddress;

    private String paymentMethod;

    private String paymentStatus;
    
    private int total;

    private int shippingPrice;

    private int totalBeforeDiscount;

    private String couponApplied;

    private int taxPrice;
    
    private String orderStatus;    

    private boolean isPaid;
    
    private LocalDateTime paidCreatedAt;

    private LocalDateTime deliveredCreatedAt;

}
