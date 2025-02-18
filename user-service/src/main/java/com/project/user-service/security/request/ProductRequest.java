package com.project.user-service.security.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.project.user-service.model.ProductColorAttribute;
import com.project.user-service.model.ProductDetails;
import com.project.user-service.model.ProductQA;
import com.project.user-service.model.ProductSizeAttribute;


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

    private ProductColorAttribute color;

    private List<ProductSizeAttribute> sizes;

    private List<ProductDetails> details;

    private List<ProductQA> questions;

    private String shippingFee;  

}
