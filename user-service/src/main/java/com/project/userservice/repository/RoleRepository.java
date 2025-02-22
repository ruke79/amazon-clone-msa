package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userservice.constants.AppRole;
import com.project.userservice.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}