package com.project.catalog_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.common.dto.CategoryDto;
import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductDetailDto;
import com.project.common.dto.ProductDto;
import com.project.common.dto.ProductQADto;
import com.project.common.dto.ProductSkuDto;
import com.project.common.dto.ProductSizeDto;
import com.project.common.dto.SubCategoryDto;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="product",  schema="product",
       indexes = {
        @Index(columnList = "name, brand, slug, category_id", name = "idx_product") })
public class Product extends BaseEntity {

    @Version
    @Column(name = "version")
    private Long version;

    @Id //@Tsid        
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "product_id")
    private Long productId;

    private String name;

    @Column(length = 1000)
    private String description;

    private String brand;

    private String slug;


    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id", referencedColumnName = "category_id", nullable=false)
    private Category category;    

    
    @JsonIgnore
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY, 
    cascade = CascadeType.PERSIST, targetEntity = ProductSubcategory.class, orphanRemoval = true)    
//     @JoinTable(name="product_subcatgeory",
//     joinColumns =  { @JoinColumn(name="product_id", referencedColumnName = "product_id") },
//     inverseJoinColumns = { @JoinColumn(name="subcategory_id", referencedColumnName = "subcategory_id")})
    private List<ProductSubcategory> subcategories = new ArrayList<>();

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = ProductDetails.class, orphanRemoval = true)
    private List<ProductDetails> details;

    
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.MERGE, targetEntity = ProductQA.class, orphanRemoval = true)    
    private List<ProductQA> questions;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSku.class, orphanRemoval = true)
    private List<ProductSku> skus;

    private String refundPolicy = "30 days";

    private float rating = 0F;

    //private int num_reviews = 0;

    private BigDecimal shipping;



    public static ProductDto convertToDto(Product product) {

        CategoryDto parent = CategoryDto.builder()
                .id(Long.toString(product.getCategory().getCategoryId()))
                .name(product.getCategory().getCategoryName())
                .slug(product.getCategory().getSlug()).build();

        List<SubCategoryDto> subCategories = product.getSubcategories().stream()
                .map(productSubcategory -> new SubCategoryDto(Long.toString(productSubcategory.getSubcategory().getSubcategoryId()), parent,
                productSubcategory.getSubcategory().getSubcategoryName(), productSubcategory.getSubcategory().getSlug()))
                .collect(Collectors.toList());

        List<ProductDetailDto> details = product.getDetails().stream().map(detail -> {
            return ProductDetailDto.builder()
                    .name(detail.getName())
                    .value(detail.getValue()).build();
        }).collect(Collectors.toList());

        
        List<ProductQADto> questions = product.getQuestions().stream().map(q -> {
            return ProductQADto.builder()
                    .question(q.getQuestion())
                    .answer(q.getAnswer())
                    .build();
        }).collect(Collectors.toList());

        List<ProductSkuDto> skus = product.getSkus().stream().map(sku -> {

            List<String> base64Image = new ArrayList<String>();
            for (String image : sku.getImages()) {                
                base64Image.add(image);
            }

            Set<ProductSizeDto> sizes = sku.getSizes().stream().map(item -> {
                ProductSizeDto size = new ProductSizeDto(Long.toString(item.getSizeId()),
                        item.getSize(), item.getQuantity(), item.getPrice());
                return size;
            }).collect(Collectors.toSet());

            ProductColorDto color = new ProductColorDto(Long.toString(sku.getColor().getColorId()),
                    sku.getColor().getColor(), sku.getColor().getColorImage());

            ProductSkuDto dto = ProductSkuDto.builder()
                    .id(Long.toString(sku.getSkuproductId()))
                    .sku(sku.getSku())
                    .images(base64Image)
                    .discount(sku.getDiscount())
                    .sold(sku.getSold())
                    .sizes(sizes)
                    .color(color)
                    .build();

            return dto;
        }).collect(Collectors.toList());

        return ProductDto.builder()
                .id(Long.toString(product.getProductId()))
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .slug(product.getSlug())
                .category(parent)
                .subCategories(subCategories)
                .details(details)                
                .questions(questions)
                .skus(skus)
                .refundPolicy(product.getRefundPolicy())
                .rating(product.getRating())                
                .shipping(product.getShipping().toPlainString())
                .createdAt(product.getCreatedAt().toString())
                .build();                
    }

}
