package com.project.chat_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.project.chat_service.model.ChatMember;

import jakarta.persistence.LockModeType;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> { 

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatMember> findByEmail(String email);

}
