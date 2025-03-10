package com.project.catalog_service.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.ProductSize;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    @Query(value="select b from ProductSku a inner join ProductSize b where " + 
    "a.skuproduct_id = b.skuproduct_id and b.skuproduct_id = :skuid and b.size = :size", nativeQuery = true)
    Optional<ProductSize> findBySkuproductIdAndSize(@Param("skuId") Long skuId, @Param("size") String size);


    Optional<ProductSize> findBySkuSkuproductIdAndSize(Long skuId,  String size);
}
