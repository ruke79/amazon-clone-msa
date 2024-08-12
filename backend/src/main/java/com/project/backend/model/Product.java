package com.project.backend.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    private String name;

    private String description;

    private String brand;

    private String slug;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=ProductCategory.class)
    @JoinColumn(name="category_name", referencedColumnName = "category_name", nullable = false)
    private ProductCategory category;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=SubCategory.class)
    @JoinColumn(name="subcategory_name", referencedColumnName = "subcategory_name", nullable = false)
    private SubCategory sub_category;

    private String details;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = Review.class)
    private Set<Review> reviews;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = ProductQA.class)
    private Set<ProductQA> qas;

    @OneToMany(mappedBy="product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSku.class)
    private Set<ProductSku> sku_products;

    private String refund_policy = "30 days";

    private int rating = 0;

    private int num_reviews = 0;

    private int shipping = 0;

}
