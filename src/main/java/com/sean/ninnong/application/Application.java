package com.sean.ninnong.application;


import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;

public interface Application {
    void applyDecision(ApplicationDecisionRequest request, Long charge);
    void cancelApply();
    ApplicationResponse toResponse();
}
