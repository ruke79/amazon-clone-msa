package com.project.backend.model;

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

    private String description;

    private String brand;

    private String slug;


    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id", referencedColumnName = "category_id", nullable=false)
    private ProductCategory category;    

    @OneToMany(fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = SubCategory.class)
    @JoinColumn(name ="product_fk" , referencedColumnName = "product_id")
    private List<SubCategory> subCategories;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            targetEntity = ProductDetails.class)
    private Set<ProductDetails> details;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = Review.class)
    private Set<Review> reviews;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    targetEntity = ProductQA.class)
    private Set<ProductQA> questions;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSku.class)
    private Set<ProductSku> sku_products;

    private String refund_policy = "30 days";

    private int rating = 0;

    private int num_reviews = 0;

    private int shipping = 0;

}
