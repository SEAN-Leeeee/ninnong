package com.sean.ninnong.team.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TeamInfoRequest {

    @NotBlank(message = "팀 이름을 비울 수 없습니다.")
    private String name;
    private String logo;
    private String region;
    private String meetingDay;
    private int membershipFee;

    private String description;

    private Boolean isRecruitingMembers;
}
