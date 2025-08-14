package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.userservice.model.WishList;

import java.util.Optional;
import java.util.List;

@Repository
public interface WishiListRepository extends JpaRepository<WishList, Long> {

    // --- 비관적 잠금 적용 ---
    // 위시리스트를 수정하거나 삭제할 때 다른 트랜잭션의 접근을 막기 위해 락을 겁니다.
    // 이 메서드를 호출하면 데이터베이스에 SELECT ... FOR UPDATE 쿼리가 실행됩니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    Optional<WishList> findById(Long id);

    // --- 낙관적 잠금 적용 ---
    // @Version 필드를 통해 낙관적 잠금을 명시적으로 적용합니다.
    // 이 메서드로 조회된 엔티티가 이후 수정될 때 @Version 필드를 기반으로 충돌을 감지합니다.
    @Lock(LockModeType.OPTIMISTIC)
    List<WishList> findByUserUserId(Long userId);
}