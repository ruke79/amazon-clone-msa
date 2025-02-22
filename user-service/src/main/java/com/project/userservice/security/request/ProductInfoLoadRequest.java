package com.project.userservice.security.request;


import java.util.List;

import com.project.userservice.dto.CategoryDto;
import com.project.userservice.dto.SubCategoryDto;
import com.project.userservice.model.ProductColorAttribute;
import com.project.userservice.model.ProductDetails;
import com.project.userservice.model.ProductQA;
import com.project.userservice.model.ProductSizeAttribute;

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

    private CategoryDto category;

   private List<SubCategoryDto> subCategories;

  
   private ProductColorAttribute color;

    private List<ProductSizeAttribute> sizes;

    private List<ProductDetails> details;

    private List<ProductQA> questions;

    private String shippingFee;  
}
