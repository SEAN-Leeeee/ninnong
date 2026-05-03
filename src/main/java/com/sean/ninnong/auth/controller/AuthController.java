package com.sean.ninnong.auth.controller;

import com.sean.ninnong.auth.dto.*;
import com.sean.ninnong.auth.security.JwtUtil;
import com.sean.ninnong.auth.service.LoginServiceImpl;
import com.sean.ninnong.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final LoginServiceImpl loginService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponseMsg> register(@Valid @RequestBody RegisterRequest request) {
        loginService.register(request);
        return ResponseEntity.ok(RegisterResponseMsg.from(request.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(loginService.refreshAccessToken(request));
    }

    @PatchMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogoutRequest request) {
        loginService.logout(request);
        return ResponseEntity.ok().build();
    }
}
