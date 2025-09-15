package com.sean.ninnong.auth.service;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.exception.*;
import com.sean.ninnong.auth.domain.RefreshToken;
import com.sean.ninnong.auth.dto.*;
import com.sean.ninnong.auth.repository.RefreshTokenRepository;
import com.sean.ninnong.auth.security.JwtUtil;
import com.sean.ninnong.user.domain.User;
import com.sean.ninnong.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
                });

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.create(request, encodedPassword);
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            Long userId = userPrincipal.getId();

            String accessToken = jwtUtil.generateAccessToken(request.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(userId);

            refreshTokenRepository.save(new RefreshToken(userId, refreshToken));

            return new LoginResponse(accessToken, refreshToken);

        } catch (UsernameNotFoundException e) {
            throw new LoginFailedException("존재하지 않는 이메일입니다.");
        } catch (BadCredentialsException e) {
            throw new LoginFailedException("이메일 또는 비밀번호가 틀렸습니다.");
        } catch (AuthenticationException e) {
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }
    @Override
    public RefreshResponse refreshAccessToken(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtil.isInvalidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        Long userId = jwtUtil.extractUserIdFromRefresh(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new RefreshTokenMismatchException();
        }

        String email = userRepository.findById(userId)
                .orElseThrow(RefreshTokenNotFoundException::new)
                .getEmail();

        String newAccessToken = jwtUtil.generateAccessToken(email);
        return new RefreshResponse(newAccessToken);
    }
    @Override
    public void logout(LogoutRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtUtil.isInvalidRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        Long userId = jwtUtil.extractUserIdFromRefresh(refreshToken);
        refreshTokenRepository.deleteByUserId(userId);
    }
}
