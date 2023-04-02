package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {
    private long id;
    private long userId;
    private long postId;
    private long repliedCommentId;
    private String content;
    private LocalDateTime createdAt;
}
