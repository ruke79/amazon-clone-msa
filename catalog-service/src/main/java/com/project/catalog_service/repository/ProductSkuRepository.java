package com.project.catalog_service.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.ProductSku;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

        // --- 비관적 잠금 적용 ---
        // 트랜잭션이 이 SKU를 조회하는 동안 다른 트랜잭션이 수정하지 못하도록 락을 겁니다.
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        Optional<ProductSku> findByProductProductIdAndSizesSizeAndColorColorId(Long productId, String size,
                        Long colorId);

        // --- 낙관적 잠금 적용 ---
        // 낙관적 잠금은 엔티티에 @Version 필드가 있을 때 자동으로 동작합니다.
        // 하지만 명시적으로 락을 걸 수도 있습니다.
        @Lock(LockModeType.OPTIMISTIC)
        List<ProductSku> findBySizesPriceBetweenAndSizesSizeAndColorColor(@Param("low_price") Integer lowPrice,
                        @Param("high_price") Integer highPrice,
                        @Param("size") String size, @Param("color") String color);

        @Query(value = "select distinct product_id from product.product_sku a " +
                        "left join product.product_size b on a.skuproduct_id = b.skuproduct_id " +
                        "left join product.product_color c on a.color_id = c.color_id " +
                        "where ((:low_price is null or :high_price is null ) or b.price between :low_price and :high_price) and (:size is null or b.size ~* :size)"
                        +
                        "and (:color is null or c.color ~* :color)", nativeQuery = true)
        List<Long> findProductIDBySizeAndPriceAndColor(@Param("low_price") BigDecimal lowPrice,
                        @Param("high_price") BigDecimal highPrice,
                        @Param("size") String size, @Param("color") String color);

        @Query(value = "select product_id from product.product_sku a " +
                        "left join product.product_size b on a.skuproduct_id = b.skuproduct_id " +
                        "left join product.product_color c on a.color_id = c.color_id " +
                        "where ((:low_price is null or :high_price is null ) or b.price between :low_price and :high_price) and (:size is null or b.size ~* :size)"
                        +
                        "and (:color is null or c.color ~* :color) " +
                        "order by a.sold desc", nativeQuery = true)
        List<Long> findProductIDBySizeAndPriceAndColorOrderBySoldDesc(@Param("low_price") Integer lowPrice,
                        @Param("high_price") Integer highPrice,
                        @Param("size") String size, @Param("color") String color);

        @Query(value = "select distinct b.color from product.product_sku a " +
                        "inner join product.product_color b on a.color_id = b.color_id " +
                        "where a.product_id IN (SELECT unnest(CAST(:productIds AS BIGINT[])))", // <-- 이 부분을 수정
                        nativeQuery = true)
        List<String> findColorsByProductId(@Param("productIds") List<Long> productIds);

        // @Query(value = "select distinct b.size from product.product_sku a " +
        // "inner join product.product_size b on a.skuproduct_id = b.skuproduct_id " +
        // "where a.product_id in :productIds and b.size <> '' and b.size is not null",
        // nativeQuery = true)
        /**
         * 특정 상품 ID 목록에 해당하는 모든 SKU의 사이즈를 중복 없이 조회합니다.
         * JPQL을 사용하여 엔티티 간의 관계를 탐색합니다.
         * 
         * @param productIds 조회할 상품 ID 목록
         * @return 해당 상품들의 모든 사이즈 목록
         */
        // @Query("SELECT DISTINCT ps.size " +
        // "FROM Product p " +
        // "JOIN p.skus psg " + // Product(p)와 ProductSku(psg) 조인
        // "JOIN psg.sizes ps " + // ProductSku(psg)와 ProductSize(ps) 조인
        // "WHERE p.productId IN :productIds ")
        @Query(value = "SELECT DISTINCT b.size FROM product.product_sku a " +
                        "INNER JOIN product.product_size b ON a.skuproduct_id = b.skuproduct_id " +
                        "WHERE a.product_id IN (SELECT unnest(CAST(:productIds AS BIGINT[])))", nativeQuery = true)
        List<String> findSizesByProductId(@Param("productIds") List<Long> productIds);

        // Mysql
        // @Query(value = "select distinct product_id from product_sku a " +
        // "left join product_size b on a.skuproduct_id = b.skuproduct_id " +
        // "left join product_color c on a.color_id = c.color_id " +
        // "where ((:low_price is null or :high_price is null ) or b.price between
        // :low_price and :high_price) and (:size is null or b.size REGEXP :size)" +
        // "and (:color is null or c.color REGEXP :color)", nativeQuery = true)
        // List<Long> findProductIDBySizeAndPriceAndColor(@Param("low_price") BigDecimal
        // lowPrice, @Param("high_price") BigDecimal highPrice,
        // @Param("size") String size, @Param("color") String color);

        // List<ProductSku>
        // findBySizesPriceBetweenAndSizesSizeAndColorColor(@Param("low_price") Integer
        // lowPrice, @Param("high_price") Integer highPrice,
        // @Param("size") String size, @Param("color") String color);

        // @Query(value = "select product_id from product_sku a " +
        // "left join product_size b on a.skuproduct_id = b.skuproduct_id " +
        // "left join product_color c on a.color_id = c.color_id " +
        // "where ((:low_price is null or :high_price is null ) or b.price between
        // :low_price and :high_price) and (:size is null or b.size REGEXP :size)" +
        // "and (:color is null or c.color REGEXP :color) " +
        // "order by a.sold desc", nativeQuery = true)
        // List<Long>
        // findProductIDBySizeAndPriceAndColorOrderBySoldDesc(@Param("low_price")
        // Integer lowPrice, @Param("high_price") Integer highPrice,
        // @Param("size") String size, @Param("color") String color);

        // @Query(value = "select distinct b.color from product_sku a " +
        // "inner join product_color b " +
        // "where a.product_id in :productIds", nativeQuery = true)
        // List<String> findColorsByProductId(@Param("productIds") List<Long>
        // productIds);

        // @Query(value = "select distinct b.size from product_sku a " +
        // "inner join product_size b " +
        // "where a.product_id in :productIds and b.size <> '' and b.size is not null",
        // nativeQuery = true)
        // List<String> findSizesByProductId(@Param("productIds") List<Long>
        // productIds);
}
