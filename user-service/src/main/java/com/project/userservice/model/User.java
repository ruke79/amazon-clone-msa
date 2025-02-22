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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User extends BaseEntity{
    @Id @Tsid    
    @Column(name = "user_id")
    private Long userId;

    // 실명
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;
    
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

    private boolean emailVerified = false;

    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;

    private String twoFactorSecret;
    private boolean isTwoFactorEnabled = false;
    private String signUpMethod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonBackReference
    @ToString.Exclude
    private Role role;
    
        
    @OneToMany(mappedBy="user", fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST,targetEntity = ShippingAddress.class)
    @JsonBackReference
    private List<ShippingAddress> shippingAddresses = new ArrayList<>();

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST,targetEntity = WishList.class)
    @JsonBackReference
    private List<WishList> wishLists;

    // @OneToMany(mappedBy="user", fetch = FetchType.EAGER,
    // cascade = CascadeType.PERSIST, targetEntity = Order.class)    
    // private List<Order> orderLists;
    //private List<Long> orderList;

    @OneToMany(mappedBy="reviewedBy", fetch = FetchType.EAGER,
    cascade = CascadeType.PERSIST, targetEntity = Review.class)    
    @JsonBackReference
    private List<Review> reviews;


    private String defaultPaymentMethod;

   
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email) {
        this.userName = userName;
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


