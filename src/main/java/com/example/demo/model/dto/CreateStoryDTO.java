package com.example.demo.model.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public record CreateStoryDTO(@NotNull(message = "Story content is required.") MultipartFile content,
                             Optional<List<String>> hashTags,
                             Optional<List<String>> personTags) {
}
