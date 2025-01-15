package com.project.chatserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.chatserver.domain.ChatMessage;

import java.util.List;

public interface ChatMesageRepository extends MongoRepository<ChatMessage, String> {




}