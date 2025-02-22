package com.project.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.Review;

import jakarta.transaction.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    List<Review> findByProductId(Long productId);

    
    @Modifying
    @Query("delete from Review where reviewId = :id")
    void deleteById(@Param("id") Long id);
}
