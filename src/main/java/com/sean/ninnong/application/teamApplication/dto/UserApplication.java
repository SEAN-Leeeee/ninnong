package com.sean.ninnong.application.teamApplication.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.common.enums.DraftLevel;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record UserApplication(String name, DraftLevel draftLevel, String requestMsg, LocalDateTime requestAt, ApplicationStatus status) {
}
