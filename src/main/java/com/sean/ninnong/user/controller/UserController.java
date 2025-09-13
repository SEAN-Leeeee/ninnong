package com.sean.ninnong.user.controller;

import com.sean.ninnong.auth.dto.UserResponse;
import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        UserResponse userResponse = userService.getMyInfo(user.getId(), user.getUsername());

        return ResponseEntity.ok().body(userResponse);
    }
}
