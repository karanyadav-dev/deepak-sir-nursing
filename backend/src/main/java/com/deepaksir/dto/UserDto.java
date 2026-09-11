package com.deepaksir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String profilePictureUrl;
    private boolean emailVerified;
    private boolean phoneVerified;
    private List<String> roles;
}