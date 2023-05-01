package com.example.demo.model.dto.post;

public record PostSearchResponseDTO(long postId, String contentUrl, int likeCount, int commentCount) {
}
