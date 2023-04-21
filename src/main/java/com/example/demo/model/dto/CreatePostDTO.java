package com.example.demo.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Data
public class CreatePostDTO {
    private String caption;
    private List<MultipartFile> content;
    private Optional<List<String>> hashtags;
    private List<String> taggedUsers;
}
