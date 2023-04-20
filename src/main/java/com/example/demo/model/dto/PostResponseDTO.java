package com.example.demo.model.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponseDTO {
    private Long id;
    private String contentUrl;
    private String caption;
    private LocalDateTime dateCreated;
    private List<String> hashtags = new ArrayList<>();
    private List<String> userTags = new ArrayList<>();
}
