// com.ticketa.backend.domain.RegisterRequest
package com.sean.ninnong.auth.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private DraftLevel draftLevel;

    public RegisterRequest(String email, String name, String nickname, String password, DraftLevel draftLevel) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.draftLevel = draftLevel;
    }
}
