package com.project.backend.dto;

import java.util.List;

import com.project.backend.model.ProductQA;
import com.project.backend.model.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDTO {

    private String id;

    private int style;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private String sku;

    private int shipping;

    private List<String> images;

    private ColorAttributeDTO color;

    private String size;

    private String category;    
    private List<String> subCategories;

    private int price;

    private int priceBefore;

    private int qty;

    private int quantity;

    

    private List<ProductDetailDTO> details; 
        
    private List<ProductQA> questions;
    
    private int discount;       

}
