package com.sean.ninnong.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DraftLevel {
    ELEMENTARY("초등"),
    MIDDLE("중등"),
    HIGH("고등"),
    UNIVERSITY("대학"),
    PROFESSIONAL("프로"),
    NONE("해당없음");

    private final String text;
}
