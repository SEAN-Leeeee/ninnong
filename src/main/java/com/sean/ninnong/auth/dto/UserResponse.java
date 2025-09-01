
package com.sean.ninnong.auth.dto;

import com.sean.ninnong.common.enums.DraftLevel;


public record UserResponse (
        Long id,
        String email,
        String name,
        String nickname,
        Long teamId,
        DraftLevel draftLevel
){


}
