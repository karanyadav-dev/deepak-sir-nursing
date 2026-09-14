package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.entity.Payment;
import com.deepaksir.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create Razorpay order
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestBody Map<String, Object> request) {

        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.getOrDefault("description", "Premium Content");
        String courseId = (String) request.getOrDefault("courseId", null);

        Map<String, Object> order = paymentService.createOrder(amount, description, courseId);
        return ResponseEntity.ok(ApiResponse.success("Order created", order));
    }

    /**
     * Verify payment after Razorpay callback
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Payment>> verifyPayment(
            @RequestBody Map<String, String> paymentData) {

        String orderId = paymentData.get("razorpay_order_id");
        String paymentId = paymentData.get("razorpay_payment_id");
        String signature = paymentData.get("razorpay_signature");

        Payment payment = paymentService.verifyPayment(orderId, paymentId, signature);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", payment));
    }

    /**
     * Get user's payment history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentHistory() {
        List<Payment> payments = paymentService.getUserPayments();
        return ResponseEntity.ok(ApiResponse.success("Payment history", payments));
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrder(
            @PathVariable String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Payment details", payment));
    }

    /**
     * Admin: Get all payments
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success("All payments", payments));
    }
}