package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SystemRole {

    USER("일반 회원"),
    ADMIN("관리자");

    private final String text;
}
