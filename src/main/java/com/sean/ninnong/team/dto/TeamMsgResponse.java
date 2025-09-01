package com.sean.ninnong.team.dto;

import java.time.LocalDateTime;

public record TeamMsgResponse(Long id, String message, LocalDateTime processedAt) {
    public static TeamMsgResponse createFrom(Long id) {
        return new TeamMsgResponse(
                id,
                "팀이 생성되었습니다.",
                LocalDateTime.now()
        );
    }

    public static TeamMsgResponse updateFrom(Long id) {
        return new TeamMsgResponse(
                id,
                "팀의 정보가 수정되었습니다.",
                LocalDateTime.now()
        );
    }

    public static TeamMsgResponse deleteFrom(Long id) {
        return new TeamMsgResponse(
                id,
                "팀이 삭제 되었습니다.",
                LocalDateTime.now()
        );
    }



}
