package com.sean.ninnong.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) {
        super("ID가 " + id + "인 지원서를 찾을 수 없습니다.");

    }
}
