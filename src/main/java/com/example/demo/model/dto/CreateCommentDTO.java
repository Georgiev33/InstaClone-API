package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateCommentDTO {
    private Long postId;
    private String content;
    private Long repliedCommentId;
    private List<String> hashtags = new ArrayList<>();
    private List<String> taggedUsers = new ArrayList<>();

}
