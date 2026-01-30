package com.sean.ninnong.exception;

public class RefreshTokenMismatchException extends RuntimeException {
    public RefreshTokenMismatchException() {
        super("Refresh Token이 일치하지 않습니다.");
    }
}
