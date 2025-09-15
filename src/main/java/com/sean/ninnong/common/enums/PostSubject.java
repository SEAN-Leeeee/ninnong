package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PostSubject {
    GUEST("게스트"),
    MATCH("교류전"),
    CONTEST("대회"),
    EVENT("이벤트"),
    FREE("자유");

    private final String text;

    }
