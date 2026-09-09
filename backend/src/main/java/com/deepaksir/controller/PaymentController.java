package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestBody Map<String, Object> orderRequest) {
        
        Map<String, Object> order = Map.of(
            "orderId", "dummy-order-id",
            "amount", orderRequest.getOrDefault("amount", 0),
            "currency", "INR"
        );
        return ResponseEntity.ok(ApiResponse.success("Order created", order));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyPayment(
            @RequestBody Map<String, String> paymentData) {
        
        return ResponseEntity.ok(ApiResponse.success("Payment verified", "SUCCESS"));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> getPaymentHistory() {
        return ResponseEntity.ok(ApiResponse.success("Payment history", null));
    }
}