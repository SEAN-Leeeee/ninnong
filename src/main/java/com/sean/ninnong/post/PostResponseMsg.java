package com.sean.ninnong.post;

import java.time.LocalDateTime;

public record PostResponseMsg(Long id, String message, LocalDateTime processedAt) {

    public static PostResponseMsg create(Long id) {
        return new PostResponseMsg(
                id,
                "게시글이 추가되었습니다.",
                LocalDateTime.now()
        );
    }

    public static PostResponseMsg modify(Long id) {
        return new PostResponseMsg(
                id,
                "게시글이 수정되었습니다.",
                LocalDateTime.now()
        );
    }
}
