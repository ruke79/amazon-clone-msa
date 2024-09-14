package com.project.backend.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
//@Data
@Getter
@Setter
@Table(name="product")
public class Product extends BaseEntity {

    @Id @Tsid          
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
    private ProductCategory category;    

    
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, 
    cascade = CascadeType.PERSIST)    
    @JoinTable(name="product_subcatgeory",
    joinColumns =  { @JoinColumn(name="product_id", referencedColumnName = "product_id") },
    inverseJoinColumns = { @JoinColumn(name="subcategory_id", referencedColumnName = "subcategory_id")})
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = ProductDetails.class, orphanRemoval = true)
    private List<ProductDetails> details;

    // cascade = CascadeType.PERSIST 빠지면 저장 안됨..
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = Review.class,orphanRemoval = true)
    private List<Review> reviews;

    
    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.MERGE, targetEntity = ProductQA.class, orphanRemoval = true)    
    private List<ProductQA> questions;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSku.class, orphanRemoval = true)
    private List<ProductSku> sku_products;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = OrderedProduct.class, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = CartProduct.class, orphanRemoval = true)
    private List<CartProduct> cartProducts;

    private String refund_policy = "30 days";

    private float rating = 0F;

    private int num_reviews = 0;

    private int shipping = 0;

}
