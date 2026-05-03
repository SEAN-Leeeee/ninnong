package com.sean.ninnong.application.teamApplication.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ApplicationDecisionRequest {

    @NotNull
    private Long applicantId;

    @NotNull
    private ApplicationStatus decision;

    @Size(max = 500)
    private String responseMsg;
}
