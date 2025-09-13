package com.sean.ninnong.post.dto;

import com.sean.ninnong.common.enums.Category;
import com.sean.ninnong.common.enums.PostSubject;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequest {

    private Category category;
    private PostSubject subject;
    private String title;
    private String content;
    private List<String> imageUrls;

}
