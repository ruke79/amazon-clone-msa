package com.project.catalog_service.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="product_size")
public class ProductSizeAttribute {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Long sizeId;

    private String size;

    private int quantity;

    private Integer price;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="skuproduct_id", referencedColumnName = "skuproduct_id", nullable=true)
    private ProductSku sku_product;
}
