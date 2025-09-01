package com.sean.ninnong.exception;

public class TeamMemberNotFoundException extends RuntimeException {
    public TeamMemberNotFoundException(Long teamId, Long userId) {
        super("팀 " +teamId + "에 속한 유저 " + userId + "를 찾을 수 없습니다");
    }
}
