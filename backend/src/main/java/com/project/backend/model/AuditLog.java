package com.project.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

import io.hypersistence.utils.hibernate.id.Tsid;

@Entity
@Data
public class AuditLog {
    @Id @Tsid
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String username;
    private Long noteId;
    private String noteContent;
    private LocalDateTime timestamp;
}

