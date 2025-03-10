package com.project.order_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.common.constants.OrderStatus;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="orders")        
public class Order extends BaseEntity {
   
   @Id @Tsid    
    @Column(name = "order_id")
    private Long orderId;

    
    private String trackingId;

    
    private Long customerId;
            
    
    @OneToMany(mappedBy="order", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = OrderProduct.class)
    private List<OrderProduct> orderedProducts;

    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="order_address_id", referencedColumnName = "order_address_id")
    private OrderAddress shippingAddress;
    

    private String paymentMethod;
    
    private int total;

    private int shippingPrice;

    private int totalBeforeDiscount;

    private String couponApplied;

    private int taxPrice;
    

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    
    
    private LocalDateTime paidCreatedAt;

    private LocalDateTime deliveredCreatedAt;

}
