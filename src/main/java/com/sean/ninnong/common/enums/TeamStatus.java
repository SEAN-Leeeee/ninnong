package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TeamStatus {

     ACTIVE("활동"),
    DELETED("탈퇴");

    private final String text;


}
