package com.example.demo.model.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PostResponseDTO {
    private Long id;
    private String contentUrl;
    private String caption;
    private LocalDateTime dateCreated;
    private List<String> hashtags = new ArrayList<>();
}
