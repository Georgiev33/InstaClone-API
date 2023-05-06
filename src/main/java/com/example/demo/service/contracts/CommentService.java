package com.example.demo.service.contracts;

import com.example.demo.model.dto.CommentResponseDTO;
import com.example.demo.model.dto.CreateCommentDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

public interface CommentService {
    @Transactional
    CommentResponseDTO createComment(CreateCommentDTO createCommentDTO, String authToken);

    void react(String authToken, long commentId, boolean status);

    void deleteComment(long commentId, String authToken);

    CommentResponseDTO getCommentById(long commentId);

    Page<CommentResponseDTO> getPageOfCommentReplies(long commentId, int page, int size);
}
