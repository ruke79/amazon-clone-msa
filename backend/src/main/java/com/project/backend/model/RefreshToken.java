package com.project.backend.model;

import java.time.Instant;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "refreshtoken")
public class RefreshToken {

    @Id @Tsid
    private long id;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    User user;

    @Column(nullable=false, unique=true)
    private String token;

    @Column(nullable=false)
    private Instant expiryDate;    
    
}
