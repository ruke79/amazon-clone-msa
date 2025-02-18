
package com.project.user-service.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import com.project.user-service.constants.PaymentResultStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PaymentResultDTO {

    private String paymentId;

    // @Enumerated(EnumType.STRING)    
    // private PayType payType;

    
    private Integer payPrice;
    
    private LocalDateTime payDateTime;

    private LocalDateTime payCancelDateTime;

    @Enumerated(EnumType.STRING)    
    private PaymentResultStatus payStatus;

    private String email;
}
