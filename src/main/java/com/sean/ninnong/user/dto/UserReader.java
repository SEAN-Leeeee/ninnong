package com.sean.ninnong.user.dto;

import com.sean.ninnong.user.dto.UserInfo;

import java.util.List;

public interface UserReader {
    UserInfo getUserInfo(Long userId);
    List<UserInfo> getUserInfoList(List<Long> userId);

}
