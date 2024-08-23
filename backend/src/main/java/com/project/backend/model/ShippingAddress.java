
package com.project.backend.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    //@GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    //@GenericGenerator(name = "native",strategy = "native")
    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

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

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST, targetEntity=Order.class)
    @JoinColumn(name="order_id", referencedColumnName = "order_id", nullable = false)
    private Order order;
}
