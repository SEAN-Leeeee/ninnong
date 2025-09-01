package com.sean.ninnong.member.dto;

import java.time.LocalDateTime;

public record MemberResponseMsg(Long memberId, String message, LocalDateTime processedAt) {

    public static MemberResponseMsg addFrom(Long memberId) {
        return new MemberResponseMsg(
                memberId,
                "멤버가 추가되었습니다.",
                LocalDateTime.now()
        );
    }

    public static MemberResponseMsg updateFrom(Long memberId) {
        return new MemberResponseMsg(
                memberId,
                "멤버의 정보가 변경되었습니다.",
                LocalDateTime.now()
        );
    }

    public static MemberResponseMsg deleteFrom(Long memberId) {
        return new MemberResponseMsg(
                memberId,
                "멤버가 팀에서 삭제되었습니다.",
                LocalDateTime.now()
        );
    }
}
