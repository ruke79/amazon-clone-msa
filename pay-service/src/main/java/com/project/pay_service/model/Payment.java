package com.project.pay_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.project.common.constants.PaymentStatus;
import com.project.common.constants.PaymentType;

import io.hypersistence.utils.hibernate.id.Tsid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="payment", uniqueConstraints = {
                @UniqueConstraint(name="UniqueOrderIdAndTrackingId", columnNames = {"orderId", "trackingId"})
                ,@UniqueConstraint(columnNames = "customerId")
     })
public class Payment extends BaseEntity{

    @Id @Tsid    
    @Column(name = "payment_id")
    private Long paymentId;

    private String paypalOrderId;

    private Long orderId;    

    private Long customerId;

    private BigDecimal amounts;

    private String trackingId;
    
    @CreatedDate
    private LocalDateTime paymentCreatedAt;

    @LastModifiedDate   
    private LocalDateTime cancelCreatedAT;

    @Enumerated(EnumType.STRING)    
    private PaymentType paymentType;    

    @Enumerated(EnumType.STRING)    
    private PaymentStatus paymentStatus;    
    
}
