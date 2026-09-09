package com.deepaksir.dto;

import com.deepaksir.entity.User;
import java.util.stream.Collectors;

public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private UserDto user;
    
    public AuthResponse() {}
    
    public AuthResponse(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = new UserDto(user);
    }
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}