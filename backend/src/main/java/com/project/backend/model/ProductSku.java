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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="product_sku")
public class ProductSku extends BaseEntity {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skuproduct_id")
    private Long skuproductId;

    private String sku;

    
    @Column(length = 100000)
    private List<String> images;
    
    //@Column(length = 20000)
    //private List<String> descriptionImages;

    private int discount = 0;

    private int sold = 0;

    @JsonIgnore
    @OneToMany(mappedBy="sku_product", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSizeAttribute.class)
    private Set<ProductSizeAttribute> sizes;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=ProductColorAttribute.class)
    @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable = true)
    private ProductColorAttribute color;


    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
    private Product product;

}
