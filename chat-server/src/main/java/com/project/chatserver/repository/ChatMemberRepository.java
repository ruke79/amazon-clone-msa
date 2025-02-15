package com.project.chatserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chatserver.model.ChatMember;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> { 

    boolean existsByEmail(String email);
    Optional<ChatMember> findByEmail(String email);

}
