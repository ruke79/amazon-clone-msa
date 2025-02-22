package com.project.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.userservice.constants.OrderStatusEnum;
import com.project.userservice.model.OrderedProduct;
import com.project.userservice.model.PaymentResult;
import com.project.userservice.model.ShippingAddress;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private UserProfileDto user;

    private List<OrderedProductDto> products;

    private AddressDto shippingAddress;

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
