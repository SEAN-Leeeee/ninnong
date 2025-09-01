package com.sean.ninnong.user;

import com.sean.ninnong.auth.dto.UserResponse;
import com.sean.ninnong.member.MemberReader;
import com.sean.ninnong.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

