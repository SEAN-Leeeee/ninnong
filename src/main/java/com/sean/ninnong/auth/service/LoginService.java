package com.sean.ninnong.auth.service;

import com.sean.ninnong.auth.dto.*;

public interface LoginService {
    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshResponse refreshAccessToken(RefreshRequest request);

    void logout(LogoutRequest request);
}
