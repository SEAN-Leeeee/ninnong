package com.sean.ninnong.application.teamApplication;

import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.common.enums.ApplicationStatus;

import java.util.List;

public interface TeamApplicationService {
    void applyWith(Long teamId, ApplicationRequest request, Long applicationId);

    void respondTo(Long teamId, Long applicationId, ApplicationStatus decision);

    List<TeamApplication> getApplicationList(Long id);
    void cancelApplication(Long teamId, Long applicationId);

    Long getPendingApplicationTeamIdByUserId(Long applicationId);
}
