package com.sean.ninnong.auth.dto;

import lombok.Getter;

@Getter
public class LogoutRequest {
    private String refreshToken;
}
