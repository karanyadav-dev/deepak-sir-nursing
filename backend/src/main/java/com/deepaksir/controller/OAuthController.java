package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.dto.AuthResponse;
import com.deepaksir.dto.UserDto;
import com.deepaksir.entity.Role;
import com.deepaksir.entity.User;
import com.deepaksir.repository.RoleRepository;
import com.deepaksir.repository.UserRepository;
import com.deepaksir.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class OAuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client-id:}")
    private String googleClientId;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @RequestBody Map<String, String> request) {

        String idTokenString = request.get("idToken");

        if (idTokenString == null || idTokenString.isEmpty()) {
            throw new RuntimeException("ID token is required");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            final String finalName = name != null ? name : email.split("@")[0];
            final String finalPictureUrl = pictureUrl;

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(finalName);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                newUser.setEmailVerified(true);
                newUser.setProfilePictureUrl(finalPictureUrl);

                Set<Role> roles = new HashSet<>();
                roleRepository.findByName(Role.RoleType.STUDENT).ifPresent(roles::add);
                newUser.setRoles(roles);

                return userRepository.save(newUser);
            });

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .profilePictureUrl(user.getProfilePictureUrl())
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .roles(user.getRoles().stream()
                            .map(r -> r.getName().name())
                            .collect(Collectors.toList()))
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Google login successful", response));

        } catch (Exception e) {
            log.error("Google login error", e);
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
    }
}