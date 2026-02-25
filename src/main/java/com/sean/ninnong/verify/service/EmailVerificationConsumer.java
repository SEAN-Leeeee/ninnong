package com.sean.ninnong.verify.service;

import com.sean.ninnong.verify.dto.EmailVerificationMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationConsumer {

    private final JavaMailSender mailSender;

    @SqsListener("email-verification-queue")
    public void handleMessage(EmailVerificationMessage message) {
        log.info("이메일 발송 시작: {}", message.getEmail());

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(message.getEmail());
        mail.setSubject("[NINNong] 이메일 인증");
        mail.setText("인증 코드: " + message.getCode());

        mailSender.send(mail);
        log.info("이메일 발송 완료: {}", message.getEmail());
    }
}
