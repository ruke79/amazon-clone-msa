package com.project.userservice.model;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="ordered_product")
public class OrderedProduct extends BaseEntity {
        
        @Id @Tsid        
        @Column(name = "order_product_id", unique = true)
        private Long  orderProductId; 

        private String name;

        private String image;

        private String size;

        private int qty;

        @ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable=false)
        private ProductColorAttribute color;

        private int price;       
        
                
        @ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
        private Product product;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
        private Order order;

}
