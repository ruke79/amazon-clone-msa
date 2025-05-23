package com.project.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import lombok.AccessLevel;

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
