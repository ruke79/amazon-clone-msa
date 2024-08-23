package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.backend.model.Product;
import com.project.backend.model.ProductDetails;

import jakarta.websocket.server.PathParam;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    public Product findBySlug(String slug);
    public Product findByName(String name);    

    @Query(value ="select a.product_id from product a inner join category b  where (:categoryRegexp is null or b.category_name REGEXP :categoryRegexp)", nativeQuery = true)
    public List<Long> findProductIDsByCategoryName(@Param("categoryRegexp") String categoryRegexp);


    @Query(value ="select a.product_id from product a inner join category b  where (:categoryId is null or b.category_id = :categoryId)", nativeQuery = true)
    public List<Long> findProductIDsByCategoryId(@Param("categoryId") Long categoryId);

    @Query(value="select a.brand from product a inner join category b  where (:categoryId is null or b.category_id = :categoryId)", nativeQuery = true)
    public List<String> findBrandsByCategoryId(@Param("categoryId") Long categoryId);
    

    @Query(value="select a.brand from product a inner join category b  where (:categoryRegexp is null or b.category_name REGEXP :categoryRegexp)", nativeQuery = true)
    public List<String> findBrandsByCategoryName(@Param("categoryRegexp") String categoryRegexp);

            
    @Query(value = "select a.* from product a "+
    "left join product_details b on a.product_id = b.product_id " +        
    "where (:name is null or a.name REGEXP :name) " + 
    "AND a.product_id in :productIds " + 
    "AND (:categoryId is null or a.category_id = :categoryId) " + 
    "AND (:brand is null or a.brand REGEXP :brand) " + 
    "AND ((:style is null or b.value REGEXP :style) " +     
    "AND (:material is null or b.value = :material) " +
    "AND (:gender is null or b.value = :gender)) " + 
    "AND (:rating is null or a.rating >= :rating)", nativeQuery = true)
    List<Product> findProductBySearchParams(@Param("name") String name, @Param("categoryId") Long categoryId,
    @Param("style") String style, @Param("brand") String brand, @Param("material") String material, 
    @Param("gender") String gender, @Param("rating") Integer rating, @Param("productIds") List<Long> productIds
    );

    @Query(value= "select a.* from product a "+
    "left join product_details b on a.product_id = b.product_id " +        
    "where (:name is null or a.name REGEXP :name) " + 
    "AND a.product_id in :productIds " + 
    "AND (:categoryId is null or a.category_id = :categoryId) " + 
    "AND (:style is null or b.value REGEXP :style) " + 
    "AND (:brand is null or a.brand REGEXP :brand) " + 
    "AND (:material is null or b.value = :material) " +
    "AND (:gender is null or b.value = :gender) " + 
    "AND (:rating is null or a.rating >= :rating) " +
    "ORDER BY a.rating DESC", nativeQuery = true)
    List<Product> findProductBySearchParamsOrderByRatingDesc(@Param("name") String name, @Param("categoryId") Long categoryId,
    @Param("style") String style, @Param("brand") String brand, @Param("material") String material, 
    @Param("gender") String gender, @Param("rating") Integer rating, @Param("productIds") List<Long> productIds
    );


    @Query(value = "select count(*) from product a "+
    "left join product_details b on a.product_id = b.product_id " +        
    "where (:name is null or a.name REGEXP :name)" + 
    "OR a.product_id in :productIds " + 
    "AND (:categoryId is null or a.category_id = :categoryId)" + 
    "AND (:style is null or b.value REGEXP :style)" + 
    "AND (:brand is null or a.brand REGEXP :brand)" + 
    "AND (:material is null or b.value = :material)" +
    "AND (:gender is null or b.value = :gender)" + 
    "AND (:rating is null or a.rating >= :rating)",  nativeQuery = true)
    int countProductsBySearchParams(@Param("name") String name, @Param("categoryId") Long categoryId,
    @Param("style") String style, @Param("brand") String brand, @Param("material") String material, 
    @Param("gender") String gender, @Param("rating") Integer rating, @Param("productIds") List<Long> productIds
    );

    
        
    
}
