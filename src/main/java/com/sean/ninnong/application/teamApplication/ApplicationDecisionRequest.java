package com.sean.ninnong.application.teamApplication;

import com.sean.ninnong.common.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class ApplicationDecisionRequest {

    public Long applicantId;
    public ApplicationStatus decision;
}
