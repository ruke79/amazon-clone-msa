package com.project.userservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import com.project.userservice.constants.PaymentResultStatus;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="payment_result")
public class PaymentResult extends BaseEntity{

    @Id @Tsid    
    @Column(name = "payment_id")
    private Long paymentId;
    

    @NotNull
    private Integer payPrice;

    @CreationTimestamp
    @Column(name = "pay_datetime")
    private LocalDateTime payDateTime;

    @LastModifiedDate
    @Column(name = "pay_cancel_datetime")
    private LocalDateTime payCancelDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_result_status", length = 60)
    private PaymentResultStatus payStatus;

    private String email;

    // @OneToMany(mappedBy = "paymentResult", fetch = FetchType.LAZY,
    // cascade = CascadeType.PERSIST,targetEntity = Order.class)
    // private List<Order> orders;

    // @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=Order.class,  optional = true)
    // @JoinColumn(name="orderNumber", referencedColumnName = "orderNumber")
    // private Order order;
}
