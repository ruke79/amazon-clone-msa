
package com.project.backend.model;

import java.util.List;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name="shipping_address")
public class ShippingAddress extends BaseEntity {

    
     @Id @Tsid    
    @Column(name = "shipping_address_id")
    private Long shippingAddressId;


    private String firstname;

    private String lastname;

    @NotBlank(message="주소는 공백이 없어야 합니다.")
    @Size(min=5, message="주소1은 적어도 5자 이상이어야 합니다.")
    private String address1;

    private String address2;

    @NotBlank(message="도시명은 공백이 없어야 합니다.")
    @Size(min=5, message="도시명은 적어도 5자 이상이어야 합니다.")
    private String city;

    @NotBlank(message="도명은 공백이 없어야 합니다.")
    @Size(min=5, message="도명 적어도 5자 이상이어야 합니다.")
    private String state;

    @NotBlank(message="우편번호는 공백이 없어야 합니다.")
    @Pattern(regexp = "(^$|[0-9]{5})", message="우편번호는 5자리 숫자이어야 합니다.")
    private String zipCode;
    
    private String country;

    private String phoneNumber;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "user_id", nullable=false)
    private User user;    
    
    @OneToMany(mappedBy = "shippingAddress", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST,targetEntity = Order.class)
    private List<Order> orders;

           
}
