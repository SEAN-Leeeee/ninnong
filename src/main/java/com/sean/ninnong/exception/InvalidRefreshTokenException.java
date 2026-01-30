package com.sean.ninnong.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 Refresh Token 입니다.");
    }
}
