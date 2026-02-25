package com.sean.ninnong.verify.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    public final EmailVerificationProducer producer;

    public void sendVerificationEmail(String email) {
        String code = generateCode();
        producer.send(email, code);
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
