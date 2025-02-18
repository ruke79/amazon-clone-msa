package com.project.user-service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.user-service.constants.AppRole;
import com.project.user-service.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}