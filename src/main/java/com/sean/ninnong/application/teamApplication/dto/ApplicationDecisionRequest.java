package com.sean.ninnong.application.teamApplication.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class ApplicationDecisionRequest {

    public Long applicant;
    public ApplicationStatus decision;

    public String responseMsg;
}
