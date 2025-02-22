package com.project.order_service.dto;

import java.time.LocalDateTime;
import java.util.List;



import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class OrderDto {

    private String orderNumber;

    private ServiceUserDto user;

    private List<OrderedProductDto> products;

    private OrderAddressDto shippingAddress;

    private String paymentMethod;

    private String paymentResult;
    
    private int total;

    private int shippingPrice;

    private int totalBeforeDiscount;

    private String couponApplied;

    private int taxPrice;

    private boolean isPaid;

    private String orderStatus;    

    
    private LocalDateTime paidAt;

    private LocalDateTime deliveredAt;

}
