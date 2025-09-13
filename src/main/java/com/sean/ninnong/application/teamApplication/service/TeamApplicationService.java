package com.sean.ninnong.application.teamApplication.service;

import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamApplicationService {
    void applyWith(Long teamId, ApplicationRequest request, Long applicationId);

    void cancelApply(Long teamId, Long applicationId);

    ApplicationResponse findMyApplication(Long applicationId);

    List<ApplicationResponse> getTeamApplications(Long teamId);

    @Transactional
    void responseTo(Long teamId, ApplicationDecisionRequest request, Long charge);
}
