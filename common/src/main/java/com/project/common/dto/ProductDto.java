package com.project.common.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductDto  {
    
        
    private String id;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private CategoryDto category;

    private List<SubCategoryDto> subCategories;

    private List<ProductDetailDto> details;
    
    //private List<ReviewDto> reviews;
    
    private List<ProductQADto> questions;
    
    private List<ProductSkuDto> skus;

    private String refundPolicy;

    private float rating;

    //private int num_reviews;

    private String shipping;    

    private String createdAt;

}
