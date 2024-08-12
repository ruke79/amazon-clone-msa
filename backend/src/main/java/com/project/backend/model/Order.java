package com.project.backend.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.project.backend.constants.OrderStatusEnum;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="orders")
public class Order extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=User.class)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    private User user;    
            
    @OneToMany(mappedBy="order", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = OrderedProduct.class)
    private Set<OrderedProduct> ordered_products;


    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=ShippingAddress.class)
    @JoinColumn(name="shipping_address_id", referencedColumnName = "shipping_addressId", nullable = false)
    private ShippingAddress shipping_address;

    private String payment_method;

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=PaymentResult.class)
    @JoinColumn(name="payment_id", referencedColumnName = "payment_id", nullable = false)
    private PaymentResult payment_result;
    
    private int total;

    private int shipping_price;

    private int total_before_discount;

    private String coupon_applied;

    private int tax_price = 0;

    private boolean is_paid = false;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum order_status;    

    
    private LocalDateTime paid_at;

    private LocalDateTime delivered_at;

}
