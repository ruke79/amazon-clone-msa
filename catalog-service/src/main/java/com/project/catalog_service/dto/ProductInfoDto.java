package com.project.catalog_service.dto;

import java.util.List;

import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.Review;

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
        
    private List<ProductQADTO> questions;

    private List<ReviewDTO> reviews;
    
    private int discount;     
    
    private String _uid;

}
