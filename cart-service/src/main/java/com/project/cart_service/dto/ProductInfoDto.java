package com.project.cart_service.dto;

import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDto {

    private String id;

    private int style;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private String sku;

    private int shipping;

    private List<String> images;

    private ColorAttributeDto color;

    private String size;

    private String category;    
    private List<String> subCategories;

    private int price;

    private int priceBefore;

    private int qty;

    private int quantity;
   

    private List<ProductDetailDto> details; 
        
    private List<ProductQADto> questions;

    //private List<ReviewDto> reviews;
    
    private int discount;     
    
    private String _uid;

}
