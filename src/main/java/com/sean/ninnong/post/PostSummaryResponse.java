package com.sean.ninnong.post;

import com.sean.ninnong.common.enums.Category;
import com.sean.ninnong.common.enums.PostSubject;

import java.time.LocalDateTime;

public record PostSummaryResponse(Long id, String title,
                                  Category category,
                                  PostSubject subject,
                                  Long writer,
                                  String nickname,
                                  LocalDateTime createdAt) {

}
