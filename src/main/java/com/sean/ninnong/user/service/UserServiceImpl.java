package com.sean.ninnong.user.service;

import com.sean.ninnong.auth.dto.UserResponse;
import com.sean.ninnong.member.service.MemberReader;
import com.sean.ninnong.user.dto.UserInfo;
import com.sean.ninnong.user.dto.UserReader;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final MemberReader memberReader;
    private final UserReader userReader;

    public UserServiceImpl(MemberReader memberReader, UserReader userReader) {
        this.memberReader = memberReader;
        this.userReader = userReader;
    }

    @Override
    public UserResponse getMyInfo(Long userId, String userEmail) {
        Long teamId = memberReader.getMyTeamId(userId);
        UserInfo userInfo = userReader.getUserInfo(userId);
        return new UserResponse(userId, userEmail, userInfo.getName(), userInfo.getNickname(), teamId, userInfo.getDraftLevel());
    }




}

