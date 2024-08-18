package com.project.backend.model;

import java.util.Set;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="cart")
public class Cart extends BaseEntity {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cart_id;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = CartProduct.class)
    private Set<CartProduct> cart_products;


    private int cart_total;
    private int total_after_discount;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=User.class)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
