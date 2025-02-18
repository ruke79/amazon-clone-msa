package com.project.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.backend.constants.OrderStatusEnum;

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
import lombok.Data;

@Data
@Entity
@Table(name="orders")
        
public class Order extends BaseEntity {
   
   @Id @Tsid    
    @Column(name = "order_id")
    private Long orderId;

    
    private String orderNumber;

    // @ManyToOne(fetch=FetchType.EAGER)
    // @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable = false)
    // private User user;    
    private Long userId;
            
    
    @OneToMany(mappedBy="order", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = OrderedProduct.class)
    private List<OrderedProduct> orderedProducts = new ArrayList<>();

    
    // @ManyToOne(fetch=FetchType.EAGER)
    // @JoinColumn(name="shipping_address_id", referencedColumnName = "shipping_address_id")
    // private ShippingAddress shippingAddress;
    private Long shippingAddressId;

    private String paymentMethod;
        
    // @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=PaymentResult.class)
    // @JoinColumn(name="payment_id", referencedColumnName = "payment_id", nullable = false)
    // private PaymentResult paymentResult;
    private Long paymentId;
    
    private int total;

    private int shippingPrice;

    private int totalBeforeDiscount;

    private String couponApplied;

    private int taxPrice = 0;

    private boolean isPaid = false;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus = OrderStatusEnum.NOT_PROCESSED ;    

    
    private LocalDateTime paidAt;

    private LocalDateTime deliveredAt;

}
