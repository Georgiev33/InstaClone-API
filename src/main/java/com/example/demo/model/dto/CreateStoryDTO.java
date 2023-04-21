package com.example.demo.model.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public record CreateStoryDTO(MultipartFile content, Optional<List<String>> hashTags,Optional<List<String>> personTags) {
}
