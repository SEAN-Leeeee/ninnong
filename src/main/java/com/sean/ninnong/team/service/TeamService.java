package com.sean.ninnong.team.service;

import com.sean.ninnong.team.domain.Team;
import com.sean.ninnong.team.dto.TeamInfoRequest;
import com.sean.ninnong.team.dto.TeamResponse;
import com.sean.ninnong.user.domain.User;

import java.util.List;

public interface TeamService {
    Team of(TeamInfoRequest teamInfo, User creator);

    Long createTeam(TeamInfoRequest teamInfo, Long creatorId);

    TeamResponse getTeam(Long teamId);

    List<TeamResponse> getTeamList();

    void updateOf(Long teamId, TeamInfoRequest teamInfo , Long userId);

    void softDeleteTeam(Long teamId, Long userId);

    void makeLeader(Long teamId, User user);


}
