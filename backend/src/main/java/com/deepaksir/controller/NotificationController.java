package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Notification;
import com.deepaksir.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        List<Notification> notifications = notificationService.getUserNotifications(limit, unreadOnly);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable UUID id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", notification));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All marked as read", "OK"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
    }

    // ADMIN: Send notification to specific user
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> sendNotification(
            @RequestBody Map<String, Object> request) {

        UUID userId = UUID.fromString((String) request.get("userId"));
        String title = (String) request.get("title");
        String message = (String) request.get("message");
        String type = (String) request.getOrDefault("type", "GENERAL");

        Notification notification = notificationService.sendNotification(userId, title, message, type);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent", notification));
    }

    // ADMIN: Send broadcast notification to all users
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> sendBroadcast(
            @RequestBody Map<String, Object> request) {

        String title = (String) request.get("title");
        String message = (String) request.get("message");
        String type = (String) request.getOrDefault("type", "ANNOUNCEMENT");

        Notification notification = notificationService.sendBroadcastNotification(title, message, type);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Broadcast sent", notification));
    }
}