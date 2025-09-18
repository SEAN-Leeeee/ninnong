package com.sean.ninnong.user.dto;

import com.sean.ninnong.user.domain.User;
import com.sean.ninnong.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserReaderImpl implements UserReader {
    private final UserRepository userRepository;

    public UserReaderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserInfo(Long userId) {
        return userRepository.findUserInfoById(userId);
    }

    @Override
    public List<User> getUserInfoList(List<Long> userIdList) {
        return userRepository.findUserInfoById(userIdList);
    }
}
