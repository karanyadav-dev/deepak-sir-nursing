package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.User;
import com.deepaksir.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    /**
     * Get all users with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        Page<User> usersPage = userRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<Map<String, Object>> users = usersPage.getContent().stream()
                .map(this::mapUser)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", users);
        response.put("totalElements", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(ApiResponse.success("Users retrieved", response));
    }

    /**
     * Get single user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.success("User retrieved", mapUser(user)));
    }

    /**
     * Suspend/Activate user
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleSuspend(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(
                user.isActive() ? "User activated" : "User suspended",
                mapUser(user)
        ));
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    /**
     * Get user stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long total = userRepository.count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", total);
        stats.put("activeUsers", total); // TODO: filter by active
        return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
    }

    private Map<String, Object> mapUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("fullName", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("emailVerified", user.isEmailVerified());
        map.put("phoneVerified", user.isPhoneVerified());
        map.put("active", user.isActive());
        map.put("profilePictureUrl", user.getProfilePictureUrl());
        map.put("roles", user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList()));
        map.put("createdAt", user.getCreatedAt());
        return map;
    }
}