package com.project.order_service.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
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
