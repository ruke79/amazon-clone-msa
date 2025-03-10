package com.project.catalog_service.model;

import java.util.Set;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="category")
public class Category extends BaseEntity {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    
    @Column(name = "category_name")
    private String categoryName;

    private String slug;

     
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = SubCategory.class)
    private Set<SubCategory> subCategories;


    
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = Product.class)
    private Set<Product> products;

}
