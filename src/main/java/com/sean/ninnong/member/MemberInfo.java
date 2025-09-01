package com.sean.ninnong.member;

import com.sean.ninnong.common.enums.DraftLevel;
import com.sean.ninnong.common.enums.Role;
import com.sean.ninnong.user.UserInfo;

import java.time.LocalDate;

public record MemberInfo(
        Long userId,
        String name,
        int backNumber,
        Role role,
        Member.Position position,
        DraftLevel draftLevel,
        LocalDate joinedAt){


    public static MemberInfo of(Member member, UserInfo userInfo) {
        return new MemberInfo(
                userInfo.getId(),
                userInfo.getName(),
                member.getBackNumber(),
                member.getRole(),
                member.getPosition(),
                userInfo.getDraftLevel(),
                member.getJoinedAt()
        );
    }

}
