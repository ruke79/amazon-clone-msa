package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.backend.model.Address;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    
    void delete(User user);

    @Query(value = "SELECT a FROM User u LEFT JOIN Address a on u.address.addressId = a.addressId WHERE u.email = :userEmail")
    Address findAddressByEmail(@Param("userEmail") String userEmail);

    
    //@Query(value = "SELECT distinct a FROM User u LEFT JOIN ShippingAddress a on a.shippingAddressId =   WHERE u.email = :userEmail")
    

}

