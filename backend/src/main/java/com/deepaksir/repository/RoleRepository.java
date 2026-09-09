package com.deepaksir.repository;

import com.deepaksir.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    
    Optional<Role> findByName(Role.RoleType name);
    
    boolean existsByName(Role.RoleType name);
}