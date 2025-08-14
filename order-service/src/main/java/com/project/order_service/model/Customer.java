package com.project.order_service.model;


import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customer", schema = "orders",
       indexes = {
        @Index(columnList = "customer_id", name = "idx_customer_id") }, 
         uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_customer_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_customer_nickname", columnNames = {"nickname"})
        }
)
@Entity
public class Customer extends BaseEntity {

    @Id
    @Column(name = "customer_id")
    private Long id;
    private String username;
    private String nickname;
    @Email
    private String email;    
}
