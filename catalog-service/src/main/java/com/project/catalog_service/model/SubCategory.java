package com.project.catalog_service.model;

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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="subcategory", 
       indexes = {
        @Index(columnList = "subcategory_name, slug", name = "idx_subcategory") })
public class Subcategory extends BaseEntity {

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
    private Category category;    

    // @JsonIgnore
    // @OneToMany(mappedBy = "subCategories", fetch= FetchType.LAZY, cascade = CascadeType.PERSIST)    
    // private List<Product> products = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "subcategory", fetch= FetchType.LAZY, cascade = CascadeType.PERSIST)    
    private List<ProductSubcategory> products = new ArrayList<>();

    public Subcategory(String name, String slug, Category category) {
        this.subcategoryName = name;
        this.slug = slug;
        this.category = category;
    }

}
