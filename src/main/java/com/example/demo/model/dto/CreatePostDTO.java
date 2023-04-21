package com.example.demo.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public record CreatePostDTO(String caption,
                            List<MultipartFile> content,
                            Optional<List<String>> hashtags,
                            Optional<List<String>> taggedUsers) {
}
