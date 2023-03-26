package com.example.demo.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
public class CreatePostDTO {
    private String caption;
    private List<MultipartFile> content;
    private List<String> hashtags;
}
