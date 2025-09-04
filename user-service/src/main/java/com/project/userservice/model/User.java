package com.project.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.AccessLevel;
import lombok.Builder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", schema = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }, 
        indexes = {
            @Index(columnList = "username, email", name = "idx_user") })
public class User extends BaseEntity{
    @Id @Tsid    
    @Column(name = "user_id")
    private Long userId;

    // 실명
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")    
    private String username;
    
    // 별명
    @Size(max = 20)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @Size(max = 120)
    @Column(name = "password")
    @JsonIgnore
    private String password;

    
    private String image;

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;

    private String twoFactorSecret;
    @Builder.Default
    private boolean isTwoFactorEnabled = false;
    private String signUpMethod;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonBackReference
    @ToString.Exclude    // 양방향 관계에서 순환 참조 방지
    private Role role;
    
        
    @Builder.Default
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = ShippingAddress.class)
    @JsonBackReference
    private List<ShippingAddress> shippingAddresses = new ArrayList<>();

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,targetEntity = WishList.class)
    @JsonBackReference
    private List<WishList> wishLists;

    // @OneToMany(mappedBy="user", fetch = FetchType.LAZY,
    // cascade = CascadeType.PERSIST, targetEntity = Order.class)    
    // private List<Order> orderLists;
    //private List<Long> orderList;

    @OneToMany(mappedBy="reviewedBy", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST, targetEntity = Review.class)    
    @JsonBackReference
    private List<Review> reviews;


    private String defaultPaymentMethod;

   
    public User(String userName, String email, String password) {
        this.username = userName;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email) {
        this.username = userName;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return userId != null && userId.equals(((User) o).getUserId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


