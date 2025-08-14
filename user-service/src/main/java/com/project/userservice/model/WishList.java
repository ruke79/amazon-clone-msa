package com.project.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="wishlist", schema="users",
       indexes = {
        @Index(columnList = "style, user_id, product_id", name = "idx_wishlist") }       
)
public class WishList extends BaseEntity{
    
    
    @Id @Tsid
    @Column(name="wishlist_id")
    private Long wishlistId;

    private String style;

    //@JsonIgnore
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=User.class)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    @JsonBackReference
    private User user;    

    //@JsonIgnore
    // @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=Product.class)
    // @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable = false)
    // private Product product;
    private Long productId;

    public WishList(String style, Long productId) {
        this.style = style;
        this.productId = productId;
    }
}