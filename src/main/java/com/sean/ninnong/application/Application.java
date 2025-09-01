package com.sean.ninnong.application;


import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.common.enums.ApplicationStatus;

public interface Application {
    void applyDecision(ApplicationStatus decision);

    void cancelApply();
    ApplicationResponse toResponse(String targetName);
}
