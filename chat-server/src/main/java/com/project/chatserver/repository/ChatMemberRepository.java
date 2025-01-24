package com.project.chatserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.chatserver.model.ChatMember;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> { 

    boolean existsByEmail(String email);
    Optional<ChatMember> findByEmail(String email);

}
