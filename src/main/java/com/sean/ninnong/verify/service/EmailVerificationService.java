package com.sean.ninnong.verify.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    public final EmailVerificationProducer producer;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long EXPIRATION_SECONDS = 300; // 5분

    public void sendVerificationEmail(String email) {
        String code = generateCode();
        // Redis에 저장 (5분 TTL)
        redisTemplate.opsForValue().set("verify:" + email, code, Duration.ofSeconds(EXPIRATION_SECONDS));
        producer.send(email, code);
    }
    public boolean verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get("verify:" + email);
        if (savedCode == null) return false;
        if (savedCode.equals(code)) {
            redisTemplate.delete("verify:" + email);
            redisTemplate.opsForValue().set("verified:" + email, "true", Duration.ofMinutes(10));
            return true;
        }
        return false;
    }

    public boolean isVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get("verified:" + email));
    }
    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
