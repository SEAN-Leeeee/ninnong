package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {

    PENDING("인증 대기"),
    ACTIVE("활동"),
    DELETED("탈퇴");

     private final String text;
}
