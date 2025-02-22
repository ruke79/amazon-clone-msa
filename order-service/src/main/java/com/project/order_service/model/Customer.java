package com.project.order_service.model;

import org.springframework.data.annotation.Id;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
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
public class Customer {

    @Id @Tsid
    private Long id;
    private String username;
    private String name;
    @Email
    private String email;    
}
