package com.sean.ninnong.application.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(Long targetId,
                                  Long teamId,
                                  ApplicationStatus status,
                                  LocalDateTime respondedAt,
                                  String responseMsg) {

    public static ApplicationResponse empty() {
        return new ApplicationResponse(null, 0L, null, null, null);
    }
}
