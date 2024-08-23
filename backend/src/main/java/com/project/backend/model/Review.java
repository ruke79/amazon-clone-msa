package com.project.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name="review")
public class Review extends BaseEntity{
    
    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private int rating = 0;

    private String review;

    private String size;

    private String fit;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=ReviewStyle.class)
    @JoinColumn(name="rstyle_id", referencedColumnName = "rstyle_id", nullable = false)
    private ReviewStyle style;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id", referencedColumnName = "product_id", nullable=false)
    private Product product;
    

}
