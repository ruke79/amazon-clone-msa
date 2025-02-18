package com.project.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.backend.constants.OrderStatusEnum;
import com.project.backend.model.OrderedProduct;
import com.project.backend.model.PaymentResult;
import com.project.backend.model.ShippingAddress;

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
