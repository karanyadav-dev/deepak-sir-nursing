package com.deepaksir.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleType name;
    
    public Role() {}
    
    public Role(RoleType name) {
        this.name = name;
    }
    
    public enum RoleType {
        STUDENT, ADMIN, INSTRUCTOR
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public RoleType getName() { return name; }
    public void setName(RoleType name) { this.name = name; }
}