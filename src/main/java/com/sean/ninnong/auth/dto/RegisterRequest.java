// com.ticketa.backend.domain.RegisterRequest
package com.sean.ninnong.auth.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotNull
    private DraftLevel draftLevel;

    public RegisterRequest(String email, String name, String nickname, String password, DraftLevel draftLevel) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.draftLevel = draftLevel;
    }
}
