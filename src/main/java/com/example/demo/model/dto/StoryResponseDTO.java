package com.example.demo.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StoryResponseDTO(Long id,
                               String contentUrl,
                               LocalDateTime dateCreated,
                               LocalDateTime expirationDate,
                               List<String> hashTags,
                               List<String> userTags) {
}
