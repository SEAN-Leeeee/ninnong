package com.sean.ninnong.team.service;

import com.sean.ninnong.exception.TeamMemberNotFoundException;
import com.sean.ninnong.exception.TeamNotFoundException;
import com.sean.ninnong.team.domain.Team;
import com.sean.ninnong.team.dto.TeamInfoRequest;
import com.sean.ninnong.team.dto.TeamResponse;
import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.member.repository.MemberRepository;
import com.sean.ninnong.member.service.MemberService;
import com.sean.ninnong.member.repository.projection.TeamMemberCount;
import com.sean.ninnong.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public TeamServiceImpl(TeamRepository teamRepository,
                           MemberRepository memberRepository,
                           MemberService memberService) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    private Team findTeamByIdOrElseThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    @Override
    public Team of(TeamInfoRequest teamInfo, Long creatorId) {
        Team team = Team.createTeam(teamInfo, creatorId);
        teamRepository.save(team);
        return team;
    }

    @Override
    public Long createTeam(TeamInfoRequest request, Long userId) {
        Team team = of(request, userId);
        memberService.add(team.getId(), userId);

        makeLeader(team.getId(), userId);

        return team.getId();
    }

    @Override
    public void makeLeader(Long teamId, Long userId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new TeamMemberNotFoundException(teamId, userId));
        member.asLeader();
    }

    @Override
    public TeamResponse getTeam(Long id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        int memberCount = memberRepository.countByTeamId(team.getId());

        return TeamResponse.of(team, memberCount);
    }

    @Override
    public List<TeamResponse> getTeamList() {
        List<Team> teamList = teamRepository.findAll();

        Map<Long, Integer> countMap = memberRepository.countGroupedByTeamId()
                .stream()
                .collect(Collectors.toMap(
                        TeamMemberCount::getTeamId,
                        TeamMemberCount::getCount
                ));


        return teamList.stream()
                .map(team -> {
                    int count = countMap.getOrDefault(team.getId(), 0);
                    return TeamResponse.of(team, count);
                })
                .toList();
    }

    @Override
    public void updateOf(Long id, TeamInfoRequest teamInfoRequest, Long userId) {

        Team team = findTeamByIdOrElseThrow(id);
        team.validateAuthorization(userId);

        team.updateInfo(teamInfoRequest);
    }

    @Override
    public void softDeleteTeam(Long id, Long userId) {
        Team team = findTeamByIdOrElseThrow(id);
        team.validateAuthorization(userId);

        team.softDeleteTeam();
        teamRepository.save(team);
    }






}
