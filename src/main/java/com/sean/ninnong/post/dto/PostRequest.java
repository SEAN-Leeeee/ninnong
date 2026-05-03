package com.sean.ninnong.post.dto;

import com.sean.ninnong.common.enums.Category;
import com.sean.ninnong.common.enums.PostSubject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PostRequest {

    @NotNull
    private Category category;

    @NotNull
    private PostSubject subject;

    @NotBlank @Size(max = 100)
    private String title;

    @NotBlank @Size(max = 5000)
    private String content;

    private List<String> imageUrls;

}
