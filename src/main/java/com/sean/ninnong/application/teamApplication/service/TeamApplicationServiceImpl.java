package com.sean.ninnong.application.teamApplication.service;


import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.application.dto.ApplicationResponse;
import com.sean.ninnong.application.teamApplication.domain.TeamApplication;
import com.sean.ninnong.application.teamApplication.dto.ApplicationDecisionRequest;
import com.sean.ninnong.application.teamApplication.dto.UserApplication;
import com.sean.ninnong.application.teamApplication.repository.TeamApplicationRepository;
import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.exception.ApplicationNotFoundException;
import com.sean.ninnong.member.service.MemberService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamApplicationServiceImpl implements TeamApplicationService{

    private final TeamApplicationRepository teamApplicationRepository;
    private final MemberService memberService;

    public TeamApplication findApplication(Long teamId, Long applicantId) {
        return teamApplicationRepository.findByTeamIdAndApplicantAndStatus(teamId, applicantId, ApplicationStatus.PENDING)
                .orElseThrow(() -> new ApplicationNotFoundException(applicantId));
    }
    public TeamApplicationServiceImpl(TeamApplicationRepository teamApplicationRepository, MemberService memberService) {
        this.teamApplicationRepository = teamApplicationRepository;
        this.memberService = memberService;
    }
    // ----------------------------------------------------------------------------------------
    @Override
    public ApplicationResponse findMyApplication(Long applicationId) {
        return teamApplicationRepository
                .findByApplicantAndStatus(applicationId, ApplicationStatus.PENDING)
                .map(TeamApplication::toResponse)
                .orElse(ApplicationResponse.empty());
    }

    @Override
    @Transactional
    public void applyWith(Long teamId, ApplicationRequest request, Long applicationId) {
        // 팀에 소속이 되어있지 않아야하고
        // 지원을 하고있지 않아야함
        TeamApplication application = TeamApplication.of(teamId, request, applicationId);
        teamApplicationRepository.save(application);

    }

    @Override
    @Transactional
    public void cancelApply(Long teamId, Long applicantId) {
        TeamApplication application = findApplication(teamId, applicantId);
        application.cancelApply();
    }

    @Override
    public List<UserApplication> getTeamApplications(Long teamId) {
        return teamApplicationRepository.findUserApplicationsByTeamId(teamId);
    }

    @Override
    @Transactional
    public void responseTo(Long teamId, ApplicationDecisionRequest request, Long charge) {
        TeamApplication application = findApplication(teamId, request.getApplicant());
        application.applyDecision(request, charge);

        if (request.getDecision() == ApplicationStatus.ACCEPT) {
            memberService.add(application.getTeamId(), request.getApplicant());
        }
    }


}
