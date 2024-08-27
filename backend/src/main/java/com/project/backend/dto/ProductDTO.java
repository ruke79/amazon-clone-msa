package com.project.backend.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductSku;
import com.project.backend.model.ProductQA;
import com.project.backend.model.Review;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.SubCategory;

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

    private String category;

    private List<String> subCategories;

    private List<ProductDetailDTO> details;
    
    private List<ReviewDTO> reviews;
    
    private List<ProductQADTO> questions;
    
    private List<ProductSkuDTO> sku_products;

    private String refund_policy;

    private float rating;

    private int num_reviews;

    private int shipping;    

}
