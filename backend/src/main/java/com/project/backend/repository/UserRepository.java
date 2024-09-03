package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.backend.model.Address;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);  // 별명으로 검색

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    void delete(User user);

    @Query(value = "SELECT a FROM User u LEFT JOIN ShippingAddress a  WHERE u.userId = :userId")
    List<ShippingAddress> findAddressesByUserUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.defaultPaymentMethod = :paymentMethod WHERE u.userId = :userId")
    int updateDefaultPaymentMethod(@Param("userId") Long userId, @Param("paymentMethod") String paymentMethod);
    // @Query(value = "SELECT distinct a FROM User u LEFT JOIN ShippingAddress a on
    // a.shippingAddressId = WHERE u.email = :userEmail")

}
