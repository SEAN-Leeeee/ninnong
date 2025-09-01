package com.sean.ninnong.auth.dto;

import java.time.LocalDateTime;

public record RegisterResponseMsg(String email, String message, LocalDateTime processedAt) {
    public static RegisterResponseMsg from(String email) {
        return new RegisterResponseMsg(
                email,
                "계정이 생성되었습니다.",
                LocalDateTime.now()
        );
    }
}
