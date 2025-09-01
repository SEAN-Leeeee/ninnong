package com.sean.ninnong.user;

import java.util.List;

public interface UserReader {
    UserInfo getUserInfo(Long userId);
    List<UserInfo> getUserInfoList(List<Long> userId);

}
