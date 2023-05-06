package com.example.demo.service.contracts;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import jakarta.transaction.Transactional;

public interface CommentService {
    @Transactional
    CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken);

    void react(String authToken, long commentId, boolean status);
}
