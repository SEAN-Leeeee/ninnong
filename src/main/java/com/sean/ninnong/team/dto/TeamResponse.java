package com.sean.ninnong.team.dto;

import com.sean.ninnong.team.Team;

public record TeamResponse(
        Long id,
        String name,
        int memberCount,
        String region,
        String logo,
        String description,
        String meetingDay,
        int membershipFee,
        Boolean isRecruitingMembers) {

    public static TeamResponse of(Team team, int memberCount) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                memberCount,
                team.getRegion(),
                team.getLogo(),
                team.getDescription(),
                team.getMeetingDay(),
                team.getMembershipFee(),
                team.getRecruitingMembers());
    }
}
