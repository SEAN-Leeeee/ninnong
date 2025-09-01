package com.sean.ninnong.user;

import com.sean.ninnong.common.enums.DraftLevel;
import lombok.Getter;

@Getter
public class UserInfo {
    private final Long id;
    private final String name;
    private final String nickname;
    private final DraftLevel draftLevel;

    public UserInfo(Long id, String name, String nickname, DraftLevel draftLevel) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.draftLevel = draftLevel;
    }
}
