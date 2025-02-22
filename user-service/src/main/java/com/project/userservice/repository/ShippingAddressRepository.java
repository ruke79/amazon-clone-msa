package com.project.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.project.userservice.model.ShippingAddress;



@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {


    List<ShippingAddress> findByUser_UserId(Long userId);

}
