package com.deepaksir.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserPrincipal {
    private UUID id;
    private String email;
    private String fullName;
}