package com.project.backend.security.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

import com.project.backend.model.ProductColorAttribute;
import com.project.backend.model.ProductDetails;
import com.project.backend.model.ProductQA;
import com.project.backend.model.ProductSizeAttribute;
import com.project.backend.model.SubCategory;

@Setter
@Getter
public class ProductRequest {

    private String name;

    private String description;

    private String brand;

    private String sku;

    private String discount;

    private String parent;

    private String category;

    private List<String> subCategories;

    private ProductColorAttribute color;

    private Set<ProductSizeAttribute> sizes;

    private Set<ProductDetails> details;

    private Set<ProductQA> questions;

    private String shippingFee;

    // private List<byte[]> images;

    // private byte[] colorImage;


}
