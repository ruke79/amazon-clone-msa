package com.project.user-service.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.project.user-service.model.ProductCategory;
import com.project.user-service.model.ProductSku;
import com.project.user-service.model.ProductQA;
import com.project.user-service.model.Review;
import com.project.user-service.model.ProductDetails;
import com.project.user-service.model.SubCategory;

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
public class ProductDTO  {
    
        
    private String id;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private CategoryDTO category;

    private List<SubCategoryDTO> subCategories;

    private List<ProductDetailDTO> details;
    
    private List<ReviewDTO> reviews;
    
    private List<ProductQADTO> questions;
    
    private List<ProductSkuDTO> sku_products;

    private String refund_policy;

    private float rating;

    private int num_reviews;

    private int shipping;    

    private String createdAt;

}
