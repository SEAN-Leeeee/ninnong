package com.sean.ninnong.verify.controller;


import com.sean.ninnong.verify.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        emailVerificationService.sendVerificationEmail(email);
        return ResponseEntity.ok("이메일 전송 요청 완료");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean result = emailVerificationService.verifyCode(email, code);
        if (result) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.badRequest().body("인증 실패 - 코드가 틀리거나 만료되었습니다");
    }
}
