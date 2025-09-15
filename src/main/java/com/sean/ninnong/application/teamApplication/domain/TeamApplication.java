package com.sean.ninnong.application.teamApplication.domain;

import com.sean.ninnong.application.Application;
import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;
import com.sean.ninnong.common.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamApplication implements Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long teamId;
    private Long charge;
    private Long applicant;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime requestAt;
    private String requestMsg;
    private String responseMsg;
    private LocalDateTime respondedAt;
    private Boolean isResponseChecked;

    public TeamApplication(Long teamId, ApplicationRequest request, Long applicant) {
        this.teamId = teamId;
        this.applicant = applicant;
        this.charge = null;
        this.status = ApplicationStatus.PENDING;
        this.requestAt = LocalDateTime.now();
        this.requestMsg = request.getRequestMsg();
        this.responseMsg = null;
        this.respondedAt = null;
        this.isResponseChecked = false;
    }

    public static TeamApplication of(Long teamId, ApplicationRequest request, Long userId) {
        return new TeamApplication(teamId, request, userId);
    }

    @Override
    public void applyDecision(ApplicationDecisionRequest request, Long charge) {
        this.charge = charge;
        this.status = request.getDecision();
        this.responseMsg = request.getResponseMsg();
        this.respondedAt = LocalDateTime.now();
    }

    public void cancelApply() {
        this.status = ApplicationStatus.CANCELED;
    }

    public ApplicationResponse toResponse( ) {
        return new ApplicationResponse(applicant, teamId, status, respondedAt, responseMsg);
    }

    public Long getTeamId() {
        return teamId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }
}
