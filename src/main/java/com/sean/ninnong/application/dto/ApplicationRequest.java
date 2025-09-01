package com.sean.ninnong.application.dto;

import com.sean.ninnong.common.enums.DraftLevel;
import lombok.Getter;

@Getter
public class ApplicationRequest {
    private String userName;
    private DraftLevel draftLevel;
    private String requestMsg;
}
