package com.sean.ninnong.application.teamApplication;

import com.sean.ninnong.application.Application;
import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.member.dto.TeamMemberRequest;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class TeamApplication implements Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long teamId;
    private Long userId;
    private Long applicantId;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime requestAt;
    private String requestMsg;
    private String responseMsg;
    private LocalDateTime respondedAt;

    public TeamApplication(Long teamId, ApplicationRequest request, Long userId) {
        this.teamId = teamId;
        this.userId = userId;
        this.status = ApplicationStatus.PENDING;
        this.requestAt = LocalDateTime.now();
        this.requestMsg = request.getRequestMsg();
        this.responseMsg = "";
        this.respondedAt = null;
    }

    public static TeamApplication of(Long teamId, ApplicationRequest request, Long userId) {
        return new TeamApplication(teamId, request, userId);
    }

    @Override
    public void applyDecision(ApplicationStatus decision) {
        this.status = decision;
    }

    @Override
    public void cancelApply() {
        this.status = ApplicationStatus.CANCELED;
    }

    @Override
    public ApplicationResponse toResponse(String name) {
        return new ApplicationResponse(teamId, name, status, requestAt, requestMsg, responseMsg, respondedAt);
    }
    public TeamMemberRequest toTeamMemberRequest() {
        return new TeamMemberRequest();
    }


    public Long getTeamId() {
        return teamId;
    }


    public ApplicationStatus getStatus() {
        return status;
    }
}
