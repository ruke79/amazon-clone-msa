package com.project.order_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet.ColorAttribute;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@SuperBuilder // @Builder 대신 @SuperBuilder 사용
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="order_product", schema = "orders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_product_orderProductId", columnNames = {"order_product_id"})
        },
        indexes = {
        @Index(columnList = "name, size, productId, price", name = "idx_order") })
public class OrderProduct extends BaseEntity {
        
        @Id @Tsid        
        @Column(name = "order_product_id", unique = true)
        private Long  orderProductId; 

        private String name;

        private String image;

        private String size;

        private int qty;

        
        private Long colorId;

        private BigDecimal price;       
        
        private Long productId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
        private Order order;

}
