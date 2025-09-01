package com.sean.ninnong.exception;

public class UnauthorizedTeamAccessException extends RuntimeException {

    public UnauthorizedTeamAccessException(Long id) {
        super(id + "팀에 대한 접근 권한이 없습니다.");
    }
}
