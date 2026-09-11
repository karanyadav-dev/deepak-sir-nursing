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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered");
        }

        // Validate phone uniqueness
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
                .orElseThrow(() -> new ApiException("Student role not found. Please contact admin."));
        roles.add(studentRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ApiException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    /**
     * Public method - used by AdminAuthController and other services.
     * Maps User entity to UserDto (excludes sensitive data).
     */
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                            .map(r -> r.getName().name())
                            .collect(Collectors.toList())
                        : new java.util.ArrayList<>())
                .build();
    }
}