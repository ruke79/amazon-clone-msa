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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="subcategory")
public class SubCategory extends BaseEntity {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_id")
    private Long subcategoryId;    

    
    @Column(name = "subcategory_name")
    private String subcategoryName;

    private String slug;

    
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id", referencedColumnName = "category_id", nullable=false)
    private ProductCategory category;    

    @JsonIgnore
    @ManyToMany(mappedBy = "subCategories", fetch= FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Product> products = new ArrayList<>();

}
