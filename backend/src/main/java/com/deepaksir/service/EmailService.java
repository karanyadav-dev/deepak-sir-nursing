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

    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify Your Email - Deepak Sir Nursing");
            message.setText("Hello,\n\n" +
                    "Please verify your email by clicking the link below:\n\n" +
                    "http://localhost:8080/api/auth/verify-email?token=" + token + "\n\n" +
                    "If you didn't sign up, ignore this email.\n\n" +
                    "Regards,\nDeepak Sir Nursing Team");

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Reset Password - Deepak Sir Nursing");
            message.setText("Click to reset password:\n\n" +
                    "http://localhost:8080/api/auth/reset-password?token=" + token + "\n\n" +
                    "Regards,\nDeepak Sir Nursing Team");

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reset email", e);
        }
    }
}