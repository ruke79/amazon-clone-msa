package com.project.userservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Setter
@Entity
@Getter
@SuperBuilder
@Table(name="review", schema="users", 
         indexes = {
          @Index(columnList = "rating, review, size, fit", name = "idx_review") }
)
public class Review extends BaseEntity{
    
    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Builder.Default
    private float rating = 0F;

    private String review;

    private String size;

    private String fit;

    private List<String> images;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=ReviewStyle.class)
    @JoinColumn(name="rstyle_id", referencedColumnName = "rstyle_id", nullable = false)
    private ReviewStyle style;

    //@JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)    
    @JsonBackReference
    private User reviewedBy;    
    

    // @JsonIgnore
    // @ManyToOne(fetch=FetchType.LAZY)
    // @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
    // private Product product;
    private Long productId;
    

}
