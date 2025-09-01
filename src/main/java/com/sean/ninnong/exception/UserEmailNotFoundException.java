package com.sean.ninnong.exception;

public class UserEmailNotFoundException extends RuntimeException  {
    public UserEmailNotFoundException() {
        super("존재하지 않는 이메일입니다.");

    }
}
