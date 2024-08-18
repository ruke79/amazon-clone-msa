package com.project.backend.dto;


import java.util.List;
import java.util.Set;

import com.project.backend.model.ProductCategory;
import com.project.backend.model.ProductSku;
import com.project.backend.model.ProductQA;
import com.project.backend.model.Review;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.SubCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private CategoryDTO category;

    private List<String> subCategories;

    private Set<ProductDetails> details;
    
    private Set<Review> reviews;
    
    private Set<ProductQA> questions;
    
    private Set<ProductSkuDTO> sku_products;

    private String refund_policy;

    private int rating;

    private int num_reviews;

    private int shipping;

}
