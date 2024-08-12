package com.project.backend.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="category")
public class ProductCategory extends BaseEntity {

    @Id    
    @Column(name = "category_name")
    private String categoryName;

    private String slug;

     
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = SubCategory.class)
    private Set<SubCategory> sub_categorys;

}
