package com.sean.ninnong.application.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponseMsg(Long id, String message, LocalDateTime processedAt) {


    public static ApplicationResponseMsg submittedApplication(Long teamId) {
        return new ApplicationResponseMsg(
                teamId,
                "에 지원했습니다.",
                LocalDateTime.now()
        );
    }

    public static ApplicationResponseMsg cancelApply(Long teamId) {
        return new ApplicationResponseMsg(
                teamId,
                " 지원을 취소했습니다.",
                LocalDateTime.now()
        );
    }
    public static ApplicationResponseMsg applyOf(Long teamId, ApplicationStatus status) {
        return new ApplicationResponseMsg(
                teamId,
                "의 지원 요청이 " + status + "되었습니다.",
                LocalDateTime.now()
        );
    }

    public static ApplicationResponseMsg hasApplied(Long teamId) {
        return new ApplicationResponseMsg(
                teamId,
                "에 지원 요청이 있습니다.",
                LocalDateTime.now()
        );
    }
}
