package com.project.backend.model;

import javax.swing.text.AttributeSet.ColorAttribute;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="ordered_product")
public class OrderedProduct extends BaseEntity {
        
        @Id @Tsid        
        @Column(name = "order_product_id")
        private Long  orderProductId; 

        private String name;

        private String image;

        private String size;

        private int quantity;

        @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=ProductColorAttribute.class)
        @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable = true)
        private ProductColorAttribute color;

        private int price;       

        @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=Product.class)
        @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable = true)
        private Product product;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

}
