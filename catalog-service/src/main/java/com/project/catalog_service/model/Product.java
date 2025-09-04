package com.project.catalog_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonIgnore;


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
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@SuperBuilder // @Builder 대신 @SuperBuilder 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="product",  schema="product",
       indexes = {
        @Index(columnList = "name, brand, slug, category_id", name = "idx_product") })
public class Product extends BaseEntity {

    

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

    @Builder.Default 
    @JsonIgnore
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY, 
    cascade = CascadeType.PERSIST, targetEntity = ProductSubcategory.class, orphanRemoval = true)    
//     @JoinTable(name="product_subcatgeory",
//     joinColumns =  { @JoinColumn(name="product_id", referencedColumnName = "product_id") },
//     inverseJoinColumns = { @JoinColumn(name="subcategory_id", referencedColumnName = "subcategory_id")})    
    private Set<ProductSubcategory> subcategories = new HashSet<>();   

    //@BatchSize(size = 10) // 10개의 Product에 대한 details를 한 번에 조회
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = ProductDetails.class, orphanRemoval = true)
    private Set<ProductDetails> details;

    //@BatchSize(size = 10) // 10개의 Product에 대한 questions를 한 번에 조회
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.MERGE, targetEntity = ProductQA.class, orphanRemoval = true)    
    private Set<ProductQA> questions;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSku.class, orphanRemoval = true)
    private Set<ProductSku> skus;

    @Builder.Default 
    private String refundPolicy = "30 days";

    @Builder.Default 
    private float rating = 0F;

    //private int num_reviews = 0;

    private BigDecimal shipping;
}
