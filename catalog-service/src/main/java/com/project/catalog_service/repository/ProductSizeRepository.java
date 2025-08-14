package com.project.catalog_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.catalog_service.model.ProductSize;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    // --- 비관적 잠금 적용 ---
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value="select b from ProductSku a inner join ProductSize b where " + 
    "a.skuproduct_id = b.skuproduct_id and b.skuproduct_id = :skuid and b.size = :size", nativeQuery = true)
    Optional<ProductSize> findBySkuproductIdAndSize(@Param("skuId") Long skuId, @Param("size") String size);

    // --- 낙관적 잠금 적용 ---
    @Lock(LockModeType.OPTIMISTIC)
    Optional<ProductSize> findBySkuSkuproductIdAndSize(Long skuId,  String size);
}