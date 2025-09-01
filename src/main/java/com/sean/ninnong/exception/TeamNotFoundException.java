package com.sean.ninnong.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long id) {
        super("ID가 " + id + "인 팀을 찾을 수 없습니다.");
    }
}
