package com.project.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="wishlist")
public class WishList {

    @Id @Tsid
    @Column(name="wishlist_id")
    private Long wishlistId;

    private String style;

    //@JsonIgnore
    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=User.class)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    private User user;    

    //@JsonIgnore
    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=Product.class)
    @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable = false)
    private Product product;



}
