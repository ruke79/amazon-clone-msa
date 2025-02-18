package com.project.user-service.security.request;


import java.util.List;

import com.project.user-service.dto.CategoryDTO;
import com.project.user-service.dto.SubCategoryDTO;
import com.project.user-service.model.ProductColorAttribute;
import com.project.user-service.model.ProductDetails;
import com.project.user-service.model.ProductQA;
import com.project.user-service.model.ProductSizeAttribute;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// create or load category , subcateory including creating product
public class ProductInfoLoadRequest {

     private String name;

    private String description;

    private String brand;

    private String sku;

    private List<String> images;

    private String discount;

    private String slug;

    private String parent;

    private CategoryDTO category;

   private List<SubCategoryDTO> subCategories;

  
   private ProductColorAttribute color;

    private List<ProductSizeAttribute> sizes;

    private List<ProductDetails> details;

    private List<ProductQA> questions;

    private String shippingFee;  
}
