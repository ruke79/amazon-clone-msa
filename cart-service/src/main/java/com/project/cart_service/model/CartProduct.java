package com.project.cart_service.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    // @ManyToOne(fetch=FetchType.LAZY)
    //     @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
    //     private Product product;
    private Long productId;

    //     @ManyToOne(fetch=FetchType.LAZY)
    //     @JoinColumn(name="color_id", referencedColumnName = "color_id", nullable=false)
    //     private ProductColorAttribute color;
    private Long colorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = true)
    private Cart cart;

    private int price;    

    private int shipping;    
    
}
