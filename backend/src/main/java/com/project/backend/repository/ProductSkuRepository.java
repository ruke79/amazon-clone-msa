package com.project.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.project.backend.model.ProductSku;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

}
