package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public record PostResponseDTO(long id,
                              List<String> contentUrl,
                              String caption,
                              LocalDateTime dateCreated,
                              List<String> hashtags,
                              List<String> userTags) {

}
