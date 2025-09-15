package com.sean.ninnong.user.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import com.sean.ninnong.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {
    private final Long id;
    private final String name;
    private final String nickname;
    private final DraftLevel draftLevel;

    @Builder
    private UserInfo(Long id, String name, String nickname, DraftLevel draftLevel) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.draftLevel = draftLevel;
    }

    public static UserInfo of(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getName())
                .draftLevel(user.getDraftLevel())
                .build();
    }

}
