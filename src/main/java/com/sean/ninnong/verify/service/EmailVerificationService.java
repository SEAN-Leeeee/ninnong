package com.sean.ninnong.verify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long EXPIRATION_SECONDS = 300; // 5분

    public void sendVerificationEmail(String email) {
        String code = generateCode();
        redisTemplate.opsForValue().set("verify:" + email, code, Duration.ofSeconds(EXPIRATION_SECONDS));

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("[NINNong] 이메일 인증");
        mail.setText("인증 코드: " + code);
        mailSender.send(mail);

        log.info("이메일 발송 완료: {}", email);
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
