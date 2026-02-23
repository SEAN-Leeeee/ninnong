package com.sean.ninnong.application.teamApplication.service;

import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;
import com.sean.ninnong.application.teamApplication.dto.UserApplication;
import com.sean.ninnong.user.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamApplicationService {
    void applyWith(Long teamId, ApplicationRequest request, Long applicantId);

    void cancelApply(Long teamId, Long applicantId);

    ApplicationResponse findMyApplication(Long applicantId);

    List<UserApplication> getTeamApplications(Long teamId);

    @Transactional
    void responseTo(Long teamId, ApplicationDecisionRequest request, Long charge);
}
