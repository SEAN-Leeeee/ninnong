package com.sean.ninnong.application.teamApplication.dto;

import com.sean.ninnong.common.enums.ApplicationStatus;
import com.sean.ninnong.user.domain.User;
import lombok.Getter;

@Getter
public class ApplicationDecisionRequest {

    public User applicant;
    public ApplicationStatus decision;

    public String responseMsg;
}
