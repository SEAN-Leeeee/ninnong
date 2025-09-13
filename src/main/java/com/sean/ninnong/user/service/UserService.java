package com.sean.ninnong.user.service;

import com.sean.ninnong.auth.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getMyInfo(Long userId, String userEmail);

}
