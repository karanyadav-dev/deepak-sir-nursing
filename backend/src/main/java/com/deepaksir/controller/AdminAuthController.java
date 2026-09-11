package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.dto.AuthRequest;
import com.deepaksir.dto.AuthResponse;
import com.deepaksir.entity.User;
import com.deepaksir.repository.UserRepository;
import com.deepaksir.security.JwtService;
import com.deepaksir.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Admin-only login. Student cannot login through this endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(
            @Valid @RequestBody AuthRequest request) {

        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check ADMIN role
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN")
                        || role.getName().name().equals("INSTRUCTOR"));

        if (!isAdmin) {
            throw new RuntimeException("Access denied: Admin credentials required");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(authService.mapToUserDto(user))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
    }

    /**
     * Verify admin token.
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAdmin(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }

        String token = authHeader.substring(7);
        String userId = jwtService.extractUserId(token);

        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Not an admin");
        }

        return ResponseEntity.ok(ApiResponse.success("Admin verified",
                Map.of("isAdmin", true, "user", user.getEmail())));
    }
}