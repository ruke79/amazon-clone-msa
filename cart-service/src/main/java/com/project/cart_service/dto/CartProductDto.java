package com.project.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProductDTO {

    private String id;
    private String name;
    private String image;
    private String size;
    private int qty;
    private ColorAttributeDto color;
    private int price;

    private int style; 

}
