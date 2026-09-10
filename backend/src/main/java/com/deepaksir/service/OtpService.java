package com.deepaksir.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_EXPIRY_MINUTES = 5;

    public String generateOtp(String phone) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(OTP_PREFIX + phone, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        log.info("OTP for {}: {}", phone, otp);
        return otp;
    }

    public boolean verifyOtp(String phone, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + phone);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(OTP_PREFIX + phone);
            return true;
        }
        return false;
    }

    public void clearOtp(String phone) {
        redisTemplate.delete(OTP_PREFIX + phone);
    }
}