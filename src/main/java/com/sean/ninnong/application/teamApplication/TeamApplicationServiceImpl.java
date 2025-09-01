package com.sean.ninnong.application.teamApplication;


import com.sean.ninnong.application.dto.ApplicationRequest;
import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.exception.ApplicationNotFoundException;
import com.sean.ninnong.team.TeamRepository;
import com.sean.ninnong.member.MemberService;
import com.sean.ninnong.member.dto.TeamMemberRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamApplicationServiceImpl implements TeamApplicationService{

    private final TeamApplicationRepository teamApplicationRepository;
    private final TeamRepository teamRepository;
    private final MemberService memberService;

    public TeamApplication findTeamApplication(Long teamId, Long applicantId) {
        return teamApplicationRepository.findByTeamIdAndUserIdAndStatus(teamId, applicantId, ApplicationStatus.PENDING)
                .orElseThrow(() -> new ApplicationNotFoundException(applicantId));
    }
    public TeamApplicationServiceImpl(TeamApplicationRepository teamApplicationRepository, TeamRepository teamRepository, MemberService memberService) {
        this.teamApplicationRepository = teamApplicationRepository;
        this.teamRepository = teamRepository;
        this.memberService = memberService;
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
    public void respondTo(Long teamId, Long applicationId, ApplicationStatus decision) {
        TeamApplication application = findTeamApplication(teamId, applicationId);
        application.applyDecision(decision);

        if (decision == ApplicationStatus.ACCEPT) {
            TeamMemberRequest request = application.toTeamMemberRequest();
            memberService.of(application.getTeamId(), request.getUserId());
        }
    }

    @Override
    public List<TeamApplication> getApplicationList(Long id) {
        return teamApplicationRepository.findAll();
    }

    @Override
    @Transactional
    public void cancelApplication(Long teamId, Long applicantId) {
        TeamApplication application = findTeamApplication(teamId, applicantId);
        application.cancelApply();
    }

    @Override
    public Long getPendingApplicationTeamIdByUserId(Long applicationId) {
         return teamApplicationRepository
                 .findByUserIdAndStatus(applicationId, ApplicationStatus.PENDING)
                 .map(TeamApplication::getTeamId)
                 .orElse(0L);
    }


}
