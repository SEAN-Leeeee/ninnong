package com.sean.ninnong.exception;

public class RefreshTokenMismatchException extends RuntimeException {
    public RefreshTokenMismatchException() {
        super("토큰이 일치하지 않습니다.");
    }
}
