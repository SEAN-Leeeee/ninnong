package com.sean.ninnong.user.dto;

import com.sean.ninnong.user.domain.User;
import com.sean.ninnong.user.dto.UserInfo;

import java.util.List;

public interface UserReader {
    User getUserInfo(Long userId);
    List<User> getUserInfoList(List<Long> userId);

}
