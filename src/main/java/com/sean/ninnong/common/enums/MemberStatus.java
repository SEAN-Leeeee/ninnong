package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("활동"),
    STOPPED("정지"),
    LEAVE("탈퇴"),
    KICKED("강퇴");

    private final String text;
}
