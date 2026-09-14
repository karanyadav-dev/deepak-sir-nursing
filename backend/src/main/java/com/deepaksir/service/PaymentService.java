package com.deepaksir.service;

import com.deepaksir.entity.Payment;
import com.deepaksir.entity.User;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.PaymentRepository;
import com.deepaksir.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${payment.razorpay.key-id:}")
    private String razorpayKeyId;

    @Value("${payment.razorpay.key-secret:}")
    private String razorpayKeySecret;

    /**
     * Create Razorpay order
     */
    @Transactional
    public Map<String, Object> createOrder(BigDecimal amount, String description, String courseId) {
        try {
            if (razorpayKeyId == null || razorpayKeyId.isEmpty()) {
                throw new ApiException("Razorpay keys not configured");
            }

            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(new BigDecimal(100)).intValue()); // paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + UUID.randomUUID().toString().substring(0, 8));
            orderRequest.put("payment_capture", 1);

            Order order = razorpay.orders.create(orderRequest);

            UUID userId = getCurrentUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException("User not found"));

            Payment payment = new Payment();
            payment.setUser(user);
            payment.setOrderId(order.get("id"));
            payment.setAmount(amount);
            payment.setCurrency("INR");
            payment.setStatus("CREATED");
            payment.setDescription(description);
            payment.setCourseId(courseId);

            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("keyId", razorpayKeyId);
            response.put("description", description);

            log.info("Order created: {} for user: {}", order.get("id"), user.getEmail());
            return response;

        } catch (RazorpayException e) {
            log.error("Razorpay error: {}", e.getMessage());
            throw new ApiException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Verify Razorpay payment signature
     */
    @Transactional
    public Payment verifyPayment(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ApiException("Order not found"));

            if (isValid) {
                payment.setPaymentId(paymentId);
                payment.setSignature(signature);
                payment.setStatus("SUCCESS");
                payment.setCompletedAt(LocalDateTime.now());
                log.info("Payment SUCCESS: {}", orderId);
            } else {
                payment.setStatus("FAILED");
                log.warn("Payment FAILED: {}", orderId);
            }

            return paymentRepository.save(payment);

        } catch (RazorpayException e) {
            log.error("Payment verification error: {}", e.getMessage());
            throw new ApiException("Payment verification failed: " + e.getMessage());
        }
    }

    /**
     * Get payment history for current user
     */
    @Transactional(readOnly = true)
    public List<Payment> getUserPayments() {
        UUID userId = getCurrentUserId();
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get all payments (admin only)
     */
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException("Payment not found"));
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ApiException("Not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
        return user.getId();
    }
}