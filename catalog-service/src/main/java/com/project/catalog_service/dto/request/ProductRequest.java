package com.project.catalog_service.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

import com.project.catalog_service.model.ProductColor;
import com.project.catalog_service.model.ProductDetails;
import com.project.catalog_service.model.ProductQA;
import com.project.catalog_service.model.ProductSize;


@Setter
@Getter
public class ProductRequest {

    private String name;

    private String description;

    private String brand;

    private String sku;

    private List<String> images;

    private String discount;

    private String slug;

    private String parent;

    private String category;

    private List<String> subCategories;

    private ProductColor color;

    private List<ProductSize> sizes;

    private Set<ProductDetails> details;

    private Set<ProductQA> questions;

    private String shippingFee;  

}
