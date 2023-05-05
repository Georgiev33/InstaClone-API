package com.example.demo.model.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public record CreatePostDTO(String caption,
                            @NotEmpty(message = "Post content must be included.") List<MultipartFile> content,
                            Optional<List<String>> hashtags,
                            Optional<List<String>> taggedUsers) {
}
