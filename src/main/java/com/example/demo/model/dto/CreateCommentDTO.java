package com.example.demo.model.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;


public record CreateCommentDTO(@Positive(message = "Invalid post id. Must be a positive integer.") long postId,
                               @NotBlank(message = "Content can't be empty or null.") String content,
                               @Positive(message = "Invalid comment id. Must be a positive integer.")Long repliedCommentId,
                               List<String> hashtags,
                               List<String> taggedUsers) {
}
