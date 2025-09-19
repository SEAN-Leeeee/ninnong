package com.sean.ninnong.user.service;

import com.sean.ninnong.auth.dto.UserResponse;
import com.sean.ninnong.member.service.MemberReader;
import com.sean.ninnong.user.domain.User;
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
        User user = userReader.getUserInfo(userId);
        UserInfo userInfo = UserInfo.of(user);

        return new UserResponse(userId, userEmail, userInfo.getName(), userInfo.getNickname(), teamId, userInfo.getDraftLevel());
    }

    @Override
    public String getNickname(Long id) {
        User userInfo = userReader.getUserInfo(id);
        return userInfo.getNickname();
    }


}
