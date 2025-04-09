package com.project.cart_service.dto;


import java.math.BigDecimal;

import com.project.common.dto.ProductColorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartProductDto {

    private String id;
    private String name;
    private String image;
    private String size;
    private int qty;
    private ProductColorDto color;
    private BigDecimal price;

    private int style; 

}
