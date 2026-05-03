package com.sean.ninnong.application.teamApplication.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.common.enums.DraftLevel;

import java.time.LocalDateTime;

public record UserApplication(Long applicantId, String name, DraftLevel draftLevel, String requestMsg, LocalDateTime requestAt, ApplicationStatus status) {
}
