package com.deepaksir.service;

import com.deepaksir.dto.AuthRequest;
import com.deepaksir.dto.AuthResponse;
import com.deepaksir.dto.RegisterRequest;
import com.deepaksir.dto.UserDto;
import com.deepaksir.entity.Role;
import com.deepaksir.entity.User;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.RoleRepository;
import com.deepaksir.repository.UserRepository;
import com.deepaksir.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthService(UserRepository userRepository, 
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered");
        }
        
        // Check phone uniqueness
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException("Phone number already registered");
        }
        
        // Create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Set default role
        Set<Role> roles = new HashSet<>();
        Role studentRole = roleRepository.findByName(Role.RoleType.STUDENT)
            .orElseThrow(() -> new ApiException("Student role not found"));
        roles.add(studentRole);
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return new AuthResponse(accessToken, refreshToken, savedUser);
    }
    
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
            .orElseThrow(() -> new ApiException("User not found"));
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(accessToken, refreshToken, user);
    }
    
    public UserDto getCurrentUser(User user) {
        return new UserDto(user);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new ApiException("User not found"));
        
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(newAccessToken, newRefreshToken, user);
    }
}