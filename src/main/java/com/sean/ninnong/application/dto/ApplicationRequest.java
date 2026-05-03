package com.sean.ninnong.application.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ApplicationRequest {
    @NotBlank
    private String userName;

    @NotNull
    private DraftLevel draftLevel;

    @Size(max = 500)
    private String requestMsg;
}
