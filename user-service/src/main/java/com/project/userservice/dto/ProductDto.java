package com.project.userservice.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.project.userservice.model.ProductCategory;
import com.project.userservice.model.ProductSku;
import com.project.userservice.model.ProductQA;
import com.project.userservice.model.Review;
import com.project.userservice.model.ProductDetails;
import com.project.userservice.model.SubCategory;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto  {
    
        
    private Long id;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private CategoryDto category;

    private List<SubCategoryDto> subCategories;

    private List<ProductDetailDto> details;
    
    private List<ReviewDto> reviews;
    
    private List<ProductQADto> questions;
    
    private List<ProductSkuDto> sku_products;

    private String refund_policy;

    private float rating;

    //private int num_reviews;

    private int shipping;    

    private String createdAt;

}
