package com.sean.ninnong.member.dto;

import com.sean.ninnong.common.enums.Role;
import lombok.Getter;

@Getter
public class TeamMemberRequest {
    private Long userId;
    private static final Role role = Role.MEMBER;
    private Long teamId;

}
