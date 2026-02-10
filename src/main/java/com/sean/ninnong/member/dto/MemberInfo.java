package com.sean.ninnong.member.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import com.sean.ninnong.common.enums.MemberPosition;
import com.sean.ninnong.common.enums.Role;
import com.sean.ninnong.member.domain.Member;
import com.sean.ninnong.user.domain.User;

import java.time.LocalDateTime;

public record MemberInfo(
        Long userId,
        String name,
        int backNumber,
        Role role,
        MemberPosition position,
        DraftLevel draftLevel,
        LocalDateTime joinedAt){


    public static MemberInfo from(Member m) {
        User user = m.getUser();
        return new MemberInfo(user.getId(),
                user.getName(),
                m.getBackNumber(),
                m.getRole(),
                m.getPosition(),
                user.getDraftLevel(),
                m.getJoinedAt()
        );
    }
}
