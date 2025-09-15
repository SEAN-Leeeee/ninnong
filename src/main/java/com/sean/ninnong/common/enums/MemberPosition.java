package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberPosition {
    GUARD("가드"),
    FORWARD("포워드"),
    CENTER("센터"),
    NONE("미정");

    private final String text;
}
