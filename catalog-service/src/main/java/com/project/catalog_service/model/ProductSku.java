package com.project.catalog_service.model;

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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder // @Builder 대신 @SuperBuilder 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="product_sku",  schema="product",
       indexes = {
        @Index(columnList = "sku, sold", name = "idx_product_sku") })
public class ProductSku extends BaseEntity {

    

    @Id //@Tsid
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skuproduct_id")
    private Long skuproductId;

    private String sku;

    
    @Column(length = 256)
    private List<String> images;
    
    //@Column(length = 20000)
    //private List<String> descriptionImages;

    @Builder.Default 
    private int discount = 0;

    @Builder.Default 
    private int sold = 0;

    @JsonIgnore
    @OneToMany(mappedBy="sku", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ProductSize.class)
    private List<ProductSize> sizes;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=ProductColor.class)
    @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable = true)
    private ProductColor color;


    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
    private Product product;

}
