package com.project.catalog_service.model;

import java.math.BigDecimal;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="product_size",  schema="product",
        indexes = {
        @Index(columnList = "size", name = "idx_product_size") })
public class ProductSize extends BaseEntity { 

    

    @Id //@Tsid
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Long sizeId;

    private String size;

    private int quantity;

    private BigDecimal price;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="skuproduct_id", referencedColumnName = "skuproduct_id", nullable=true)
    private ProductSku sku;
}
