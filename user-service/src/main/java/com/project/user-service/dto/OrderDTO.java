package com.project.user-service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.user-service.constants.OrderStatusEnum;
import com.project.user-service.model.OrderedProduct;
import com.project.user-service.model.PaymentResult;
import com.project.user-service.model.ShippingAddress;

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
public class OrderDTO {

    private String orderNumber;

    private UserProfileDTO user;

    private List<OrderedProductDTO> products;

    private AddressDTO shippingAddress;

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
