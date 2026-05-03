package com.sean.ninnong.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequest {

    @NotBlank @Size(max = 1000)
    private final String content;
    private final Long parentId;

    private CommentRequest(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }

}
