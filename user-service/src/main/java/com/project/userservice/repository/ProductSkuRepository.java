package com.project.userservice.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.project.userservice.model.ProductSku;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    
    @Query(value = "select product_id from product_sku a " +
    "left join product_size b on a.skuproduct_id = b.skuproduct_id " +
    "left join product_color c on a.color_id = c.color_id " +
    "where ((:low_price is null or :high_price is null ) or b.price between :low_price and :high_price) and (:size is null or b.size REGEXP :size)" +
    "and (:color is null or c.color REGEXP :color)", nativeQuery = true)
    List<Long> findProductIDBySizeAndPriceAndColor(@Param("low_price") Integer lowPrice, @Param("high_price") Integer highPrice,
    @Param("size") String size, @Param("color") String color);

    List<ProductSku> findBySizesPriceBetweenAndSizesSizeAndColorColor(@Param("low_price") Integer lowPrice, @Param("high_price") Integer highPrice,
    @Param("size") String size, @Param("color") String color);

    @Query(value = "select product_id from product_sku a " +
    "left join product_size b on a.skuproduct_id = b.skuproduct_id " +
    "left join product_color c on a.color_id = c.color_id " +
    "where ((:low_price is null or :high_price is null ) or b.price between :low_price and :high_price) and (:size is null or b.size REGEXP :size)" +
    "and (:color is null or c.color REGEXP :color) " +
    "order by a.sold desc", nativeQuery = true)
    List<Long> findProductIDBySizeAndPriceAndColorOrderBySoldDesc(@Param("low_price") Integer lowPrice, @Param("high_price") Integer highPrice,
    @Param("size") String size, @Param("color") String color);

 
    @Query(value = "select distinct b.color from product_sku a " +    
    "inner join product_color b " +
    "where a.product_id in :productIds", nativeQuery = true)
    List<String> findColorsByProductId(@Param("productIds") List<Long> productIds);

    @Query(value = "select distinct b.size from product_sku a " +    
    "inner join product_size b " +
    "where a.product_id in :productIds and b.size <> '' and b.size is not null", nativeQuery =  true)
    List<String> findSizesByProductId(@Param("productIds") List<Long> productIds);
}
