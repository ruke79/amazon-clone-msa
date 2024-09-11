package com.project.backend.model;

import java.util.ArrayList;
import java.util.List;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="product_color")
public class ProductColorAttribute {

    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "color_id")
    private Long colorId;
    private String color;
       
    //@Lob
    //@Column(length = 1048576)
    private String colorImage;


    @OneToMany(mappedBy="color", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,
    targetEntity = OrderedProduct.class, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts;
}
