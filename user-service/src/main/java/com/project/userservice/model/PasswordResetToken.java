package com.project.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;

import java.time.Instant;

import io.hypersistence.utils.hibernate.id.Tsid;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="password_reset_token", schema="users",  
       indexes = {
        @Index(columnList = "token, user_id", name = "idx_password_reset_token") }
)
public class PasswordResetToken {

    @Version
    @Column(name = "version")
    private Long version;
    
    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PasswordResetToken(String token, Instant expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }
}