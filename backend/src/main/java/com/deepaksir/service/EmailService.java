package com.deepaksir.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@deepaksir.com}")
    private String fromEmail;

    @Value("${app.base-url:https://deepak-sir-nursing-production.up.railway.app}")
    private String baseUrl;

    /**
     * Send email verification link
     */
    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify Your Email - Deepak Sir Nursing");
            message.setText("Hello,\n\n" +
                    "Welcome to Deepak Sir Nursing App!\n\n" +
                    "Please verify your email by clicking the link below:\n\n" +
                    baseUrl + "/api/auth/verify-email?token=" + token + "\n\n" +
                    "If you didn't sign up, please ignore this email.\n\n" +
                    "Regards,\n" +
                    "Deepak Sir Nursing Team");

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send password reset link
     */
    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Reset Password - Deepak Sir Nursing");
            message.setText("Hello,\n\n" +
                    "You requested to reset your password.\n\n" +
                    "Click the link below to reset:\n\n" +
                    baseUrl + "/api/auth/reset-password?token=" + token + "\n\n" +
                    "This link expires in 30 minutes.\n\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Regards,\n" +
                    "Deepak Sir Nursing Team");

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send general notification email
     */
    public void sendNotificationEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Notification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send notification email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}