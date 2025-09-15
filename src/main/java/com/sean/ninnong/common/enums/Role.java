package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    LEADER("주장"),
    MEMBER("멤버"),
    GUEST("게스트");
    private final String text;
    }
