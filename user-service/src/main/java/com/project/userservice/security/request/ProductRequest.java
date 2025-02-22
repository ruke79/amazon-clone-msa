package com.project.userservice.security.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.project.userservice.model.ProductColorAttribute;
import com.project.userservice.model.ProductDetails;
import com.project.userservice.model.ProductQA;
import com.project.userservice.model.ProductSizeAttribute;


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
