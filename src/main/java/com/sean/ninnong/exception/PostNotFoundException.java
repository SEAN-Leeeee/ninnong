package com.sean.ninnong.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("ID가 " + id + "인 게시글을를 찾을 수 없습니다.");

    }
}
