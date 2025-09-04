package com.project.common.dto;

import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductInfoDto {

    private String id;

    private int style;

    private String name;

    private String description;

    private String brand;

    private String slug;

    private String sku;

    private String shipping;

    private List<String> images;

    private ProductColorDto color;

    private String size;

    private String category;    
    private List<String> subCategories;

    private String price;

    private String priceBefore;

    private int qty;

    private int quantity;   

    private List<ProductDetailDto> details; 
        
    private List<ProductQADto> questions;
        
    private int discount;     
    
    private String _uid;


    public ProductInfoDto(ProductInfoDto src) {
        this.id = src.getId();
        this.name = src.getName();
        this.style = src.getStyle();
        this.brand = src.getBrand();
        this.slug = src.getSlug();
        this.sku = src.getSku();
        this.category = src.getCategory();
        this.subCategories = src.getSubCategories();
        this.description = src.getDescription();
        this.color = src.getColor();
        this.size = src.getSize();
        this.details = src.getDetails();
        this.qty = src.getQty();
        this.quantity = src.getQuantity();
        this.discount = src.getDiscount(); 
        this.images = src.getImages();
        this.questions = src.getQuestions();
        this.shipping = src.getShipping();
        this.price = src.getPrice();
        this.priceBefore = src.getPriceBefore();
        this._uid = src.get_uid();
    }

}
