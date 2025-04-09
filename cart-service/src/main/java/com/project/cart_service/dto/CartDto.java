package com.project.cart_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartDto {

    private String userId;
    private String userImage;
    

    private List<CartProductDto> products;

    private BigDecimal cartTotal;
    private BigDecimal totalAfterDiscount;

}
