package com.sean.ninnong.auth.service;

import com.sean.ninnong.auth.dto.LoginRequest;
import com.sean.ninnong.auth.dto.LoginResponse;
import com.sean.ninnong.auth.dto.LogoutRequest;
import com.sean.ninnong.auth.dto.RefreshRequest;
import com.sean.ninnong.auth.dto.RefreshResponse;
import com.sean.ninnong.auth.dto.RegisterRequest;

public interface LoginService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    RefreshResponse refreshAccessToken(RefreshRequest request);
    void logout(LogoutRequest request);
}
