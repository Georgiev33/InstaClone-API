package com.example.demo.model.dto;

import java.time.LocalDateTime;


public record CommentResponseDTO(long id,
                                 long userId,
                                 long postId,
                                 Long repliedCommentId,
                                 String content,
                                 LocalDateTime createdAt,
                                 long likes,
                                 long dislikes) {
}
