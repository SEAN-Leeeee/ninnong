package com.sean.ninnong.application.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(Long targetId, String targetName, ApplicationStatus status, LocalDateTime createdAt, String requestText, String responseText, LocalDateTime responseAt) {

}
