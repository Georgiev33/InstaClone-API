package com.example.demo.model.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateStoryDTO(List<MultipartFile> content, List<String> hashtags) {
}
