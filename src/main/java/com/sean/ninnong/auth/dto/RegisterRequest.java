// com.ticketa.backend.domain.RegisterRequest
package com.sean.ninnong.auth.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private DraftLevel draftLevel;

}
