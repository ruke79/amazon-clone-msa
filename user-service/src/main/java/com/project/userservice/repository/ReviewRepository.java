package com.project.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.userservice.model.Review;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // --- 비관적 잠금 적용 ---
    // 리뷰를 조회하여 수정 또는 삭제할 때 락을 걸어 다른 트랜잭션을 차단합니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> findById(Long id);
    
    // --- 낙관적 잠금 적용 ---
    @Lock(LockModeType.OPTIMISTIC)
    List<Review> findByProductId(Long productId);

    @Modifying
    @Query("delete from Review where reviewId = :id")
    void deleteById(@Param("id") Long id);
}