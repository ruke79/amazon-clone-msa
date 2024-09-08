package com.project.backend.model;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="cart_product")
public class CartProduct extends BaseEntity {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartproduct_id")
    private Long cartproductId;

    private String _uid;

    private String name;

    private String image;

    private String size;

    private int style;

    private int qty;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=Product.class)
    @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable = true)
    private Product product;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.MERGE, targetEntity=ProductColorAttribute.class)
    @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable = true)
    private ProductColorAttribute color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = true)
    private Cart cart;

    private int price;    

    private int shipping;
    
    
}
