package com.project.catalog_service.dto.request;


import java.util.List;
import java.util.Set;

import com.project.common.dto.CategoryDto;
import com.project.common.dto.SubCategoryDto;
import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.ProductSize;

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

  
   private ProductColor color;

    private List<ProductSize> sizes;

    private Set<ProductDetails> details;

    private Set<ProductQA> questions;

    private String shippingFee;  
}
