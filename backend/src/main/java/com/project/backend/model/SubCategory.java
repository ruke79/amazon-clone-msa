package com.project.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="subcategory")
public class SubCategory extends BaseEntity {

    @Id
    @Column(name = "subcategory_name")
    private String subcategoryName;

    private String slug;

    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_name", referencedColumnName = "category_name", nullable=false)
    private ProductCategory category;

}
